(ns clokey.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.string :as string]
            [clokey.user :as user]
            [clokey.utils :as utils]
            ; ↓ these are for buddy.auth
            [compojure.response :refer [render]]
            [clojure.java.io :as io]
            [ring.util.response :refer [response redirect content-type]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]

            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]])
  (:import  [org.bson.types.ObjectId]))


; <editor-fold> -------- AUTHENTICATION -----------

(use 'ring.util.json-response)

;; AUTHENTICATION

; login

(defn home
  [request]
  (println authenticated?)
  (if-not (authenticated? (:session request))
    (throw-unauthorized)
    (let [content (slurp (io/resource "mainpage.html"))]
      (render content request))))

(defn login
  [request]
  (let [content (slurp (io/resource "login.html"))]
    (render content request)))

(defn logout
  [request]
  (-> (redirect "/login")
      (assoc :session {})))

; auth

(defn login-authenticate
  "Check request username and password against authdata
  username and passwords.
  On successful authentication, set appropriate user
  into the session and redirect to the value of
  (:next (:query-params request)). On failed
  authentication, renders the login page."
  [request]
  (let [username (get-in request [:form-params "username"])
        password (get-in request [:form-params "password"])
        session (:session request)
        found-password (:mpw (user/get-user username))]
    ;TODO: do this with encryption
    (println (keys request))
    (if (and found-password (= found-password password))
      (let [updated-session (assoc session :identity (keyword username))]
        (-> (redirect "/")
            (assoc :session updated-session)))
      (let [content (slurp (io/resource "login.html"))]
        (render content request)))))

; </editor-fold>

;; define routes
(defroutes app-routes

  ;; NAVIGATION
  (GET "/" [] home)
  (GET "/login" [] login)
  (POST "/login" [] login-authenticate)
  (GET "/logout" [] logout)

  ; CREATE
  (POST "/create-user" [username email mpw]
    (user/create-user username email mpw))
  (POST "/create-entry" [user source username password]
    (user/set-entry user (user/create-entry source username password)))

  ; READ
  (GET "/get-user" [username]
    (json-response (user/remove-id (user/get-user username))))
  (GET "/get-entry" [username source-name]
    (json-response (user/get-entry username source-name)))

  ; UPDATE
  (PUT "/update-user" [username userdata]
    (user/update-user username userdata))
  (PUT "/update-entry" [username source-name new-data]
    (user/update-entry username source-name new-data))

  ; DELETE
  (DELETE "/delete-user" [username]
    (user/delete-user username))
  (DELETE "/delete-entry" [username source-name]
    (user/delete-entry username source-name))

  (route/not-found "Not Found")

  ;; Spickzettel

  (GET "/hello-there" [] "General Kenobi")
  (GET "/user/:id" [id]
    (str "<h1>HI USER NR " id "</h1>"))
  (GET "/encrypt/:pw" [pw]
    (.println System/out "test")
    (utils/encrypt pw))
  (POST "/" request
    (str request))
  (GET "/foobar" [x y & z]
    (str x ", " y ", " z))
  (POST "/create-user" [username email mpw]
    (println username " " mpw)))

(defn unauthorized-handler
  [request metadata]
  (cond
    ;; If request is authenticated, raise 403 instead
    ;; of 401 (because user is authenticated but permission
    ;; denied is raised).
    (authenticated? request)
    (-> (render (slurp (io/resource "error.html")) request)
        (assoc :status 403))
    ;; In other cases, redirect the user to login page.
    :else
    (let [current-url (:uri request)]
      (redirect (format "/login?next=%s" current-url)))))

;; Create an instance of auth backend.

(def auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))

(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))
