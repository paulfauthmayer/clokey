(ns clokey.user
  (:require [crypto.password.bcrypt :as password]
            [clojure.string :as string]
            [clokey.pass :as pass]))
;; EXAMPLE USERS

(def example-user
  {:name "Alex da G"
   :mpw "encrypted-pw"
   :accounts
   [{:source "facebook.com"
     :username "alexdag"
     :pw "encrypted-pw"}
    {}
    {}]})

(def example-user-encrypted
  {:name "Max Mustermann"
   :mpw (pass/create-password "master-pw")
   :accounts
   [{:source "facebook.com"
     :username "max"
     :pw (pass/create-password "fb-pw")}
    {}
    {}]})

;; CRUD

(defn create-user []
  (* 1 1))

(defn get-user []
  (* 1 1))

(defn delete-user []
  (* 1 1))

(defn update-user []
  (* 1 1))

;; AUTHENTICATION

(defn authenticate []
  (* 1 1))

(defn authenticated? []
  (* 1 1))
