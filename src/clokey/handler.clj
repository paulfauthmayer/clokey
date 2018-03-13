(ns clokey.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.string :as string]
            [clokey.user :as user]
            [clokey.utils :as utils]
            [buddy.hashers :as hashers]
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

(use 'ring.util.json-response)

; <editor-fold> -------- AUTHENTICATION -----------


;; AUTHENTICATION

; Login

(defn home
  [request]
  (println authenticated?)
  (if-not (authenticated? (:session request))
    (redirect "/login")
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
    (if (and
         found-password
         (hashers/check password found-password))
      (let [updated-session (assoc session :identity (keyword username))]
        (-> (redirect "/")
            (assoc :session updated-session)))
      (let [content (slurp (io/resource "login.html"))]
        (render content request)))))

; </editor-fold>

;; define routes
(defn get-identity
  "Get the currently logged in username"
  [request]
  (-> (:session request)
      (:identity ,,,)))

(defroutes app-routes

  ;; NAVIGATION
  (GET "/" [] home)
  (GET "/login" [] login)
  (POST "/login" [] login-authenticate)
  (GET "/logout" [] logout)

  ; READ
  (GET "/get-current-user" [:as r]
    (-> (get-identity r)
        (user/get-user ,,,)
        (user/remove-id ,,,)
        (user/remove-mpw ,,,)
        (user/sort-entries ,,,)
        (json-response ,,,)))
  (GET "/get-entry" [source-name :as r]
    (-> (get-identity r)
        (user/get-entry ,,, source-name)
        (json-response ,,,)))

  ; CREATE
  (POST "/create-user" [username email mpw :as r]
    (user/create-user username email mpw)
    (-> (redirect "/")
        (assoc :session {:identity (keyword username)})))

  (POST "/create-or-update-entry" [source username password :as r]
    (let [user (get-identity r)]
      (if (empty? (user/get-entry user source))
        (do
          (println "empty true")
          (some->>  (user/create-entry source username password)
                    (user/set-entry user ,,,)))
        (do
          (println "empty false")
          (some->>  (user/create-entry source username password)
                    (user/update-entry user source ,,,)))))
    (redirect "/"))

  ; UPDATE
  (PUT "/update-user" [userdata :as r]
    (-> (get-identity r)
        (user/update-user ,,, userdata)))

  ; DELETE
  (DELETE "/delete-user" [:as r]
    (-> (get-identity r)
        (user/delete-user ,,,)))
  (DELETE "/delete-entry" [source :as r]
    (-> (get-identity r)
        (user/delete-entry ,,, source)))

  ; ELSE
  (route/not-found "Not Found"))

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
