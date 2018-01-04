(ns clokey.core
  (:require [crypto.password.bcrypt :as password]
            [clojure.string :as string]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; Core Functionality

; CRUD + Validation

(defn create-password []
  (* 1 1))

(defn read-password []
  (* 1 1))

(defn update-password []
  (* 1 1))

(defn delete-passward []
  (* 1 1))

(defn valid? []
  (* 1 1))

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

(defn create-user []
  (* 1 1))

(defn get-user []
  (* 1 1))

(defn delete-user []
  (* 1 1))

(defn update-user []
  (* 1 1))

; Security

(defn encrypt []
  (* 1 1)) ;--> bcrypt

(def encrypted (password/encrypt "foobar"))

(def check (password/check "foobar" encrypted))

(defn decrypt []
  (* 1 1))

(defn authenticate []
  (* 1 1))

(defn authenticated? []
  (* 1 1))
