(ns clokey.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.string :as string]
            [clokey.user :as user]
            [clokey.utils :as utils])
  (:import  [org.bson.types.ObjectId]))



;; define routes
(defroutes app-routes
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
    (println username " " mpw))

  ; CREATE
  (POST "/create-user") [username email mpw]
    (user/create-user username email mpw)
  (POST "/create-entry") [user source username password]
    (set-entry user (create-entry source username password))

  ; READ
  (GET "/get-user" [username]
    (user/get-user username))
  (GET "/get-entry" [username source-name]
    (user/get-entry username source-name))

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

  (route/not-found "Not Found"))


(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))
