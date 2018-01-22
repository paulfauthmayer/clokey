(ns clokey.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.string :as string]
            [clokey.user :as user]))


(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/hello-there" [] "General Kenobi")
  (GET "/user/:id" [id]
    (str "<h1>HI USER NR " id "</h1>"))
  (GET "/encrypt/:pw" [pw]
    (user/encrypt pw))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
