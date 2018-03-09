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
  

  ; READ
  (GET "/get-user/:username" [username]
    (str (user/get-user username)))
  (GET "/get-entry/:entry" [username source-name]
    (str (user/get-entry username source-name)))

  ; UPDATE


  ; DELETE
  (DELETE "/delete-user/:username" [username]
    (user/delete-user username))
  (DELETE "/delete-entry" [username source-name]
    (user/delete-entry username source-name))

  (route/not-found "Not Found"))


(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))
