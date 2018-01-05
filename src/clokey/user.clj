(ns clokey.user
  (:require [crypto.password.bcrypt :as password]
            [clojure.string :as string]
            [clokey.pass :as pass]))
;; EXAMPLE USERS

(def example-user
  {:name "Alex da G"
   :mpw "encrypted-pw"
   :entries
   [{:source "facebook.com"
     :username "alexdag"
     :pw "encrypted-pw"}
    {}
    {}]})

(def example-user-encrypted
  {:name "Max Mustermann"
   :mpw (pass/create-password "master-pw")
   :entries
   [{:source "facebook.com"
     :username "max"
     :pw (pass/create-password "fb-pw")}
    {}
    {}]})

;; CRUD

(defn create-user [username mpw]
  {:name username
   :mpw mpw
   :accounts
   []})

(defn get-user []
  (* 1 1))

(defn delete-user []
  (* 1 1))

(defn update-user []
  (* 1 1))

;; MANAGE

(defn get-entry [user source-name]
  (let [entries (user :entries)]
    (filter #(re-matches (:source %) (re-pattern source-name) entries))))

;; AUTHENTICATION

(defn authenticate []
  (* 1 1))

(defn authenticated? []
  (* 1 1))
