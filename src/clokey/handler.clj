(ns clokey.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.string :as string]
            [clokey.user :as user]
            [clokey.utils :as utils]))


(defroutes app-routes
  (GET "/hello-there" [] "General Kenobi")
  (GET "/user/:id" [id]
    (str "<h1>HI USER NR " id "</h1>"))
  (GET "/encrypt/:pw" [pw]
    (utils/encrypt pw))
  (POST "/" request
    (str request))
  (GET "/foobar" [x y & z]
    (str x ", " y ", " z))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
