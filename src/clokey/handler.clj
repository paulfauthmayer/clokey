(ns clokey.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.string :as string]
            [clokey.user :as user]
            [clokey.utils :as utils]
            [monger.core :as mg]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress]
           org.bson.types.ObjectId))

;; initialize mongodb

(let [conn (mg/connect)
      db   (mg/get-db conn "clokey-db")]

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
      (println username " " mpw)
      (let [new-user (user/create-user username email mpw)]
        (mc/insert-and-return db "users" new-user)))

    (route/not-found "Not Found"))


  (def app
    (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false))))
