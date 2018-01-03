(ns clokey.core
  (:gen-class
    (:import [java.util.Date]
             [java.time])
    (:require '[crypto.password.bcrypt :as password]
              '[clojure.string :as string])))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; Core Functionality

; CRUD + Validation

(defn create-password)

(defn read-password)

(defn update-password)

(defn delete-passward)

(defn valid?)

; User Management

(def example-user
  {:name "Alex da G"
   :mpw "encrypted-pw"
   :accounts
   [{:source "facebook.com"
     :username "alexdag"
     :pw "encrypted-pw"}
    {}
    {}]})

(defn create-user)

(defn get-user)

(defn delete-user)

(defn update-user)

; Security

(defn encrypt) ;--> bcrypt

(def encrypted (crypto.password.bcrypt/encrypt "foobar"))

(defn decrypt)

(defn authenticate)

(defn authenticated?)
