(ns clokey.user
  (:require [crypto.password.bcrypt :as password]
            [clojure.string :as string]))

;; EXAMPLE USERS

(def example-user
  {:name "Alex da G"
   :mpw "encrypted-pw"
   :entries
   [{:source "facebook.com"
     :username "alexdag"
     :pw "encrypted-pw"}
    {:source "youtube.com"
      :username "alexdag"
      :pw "encrypted-pw"}
    {:source "myspace.com"
      :username "alexdag"
      :pw "encrypted-pw"}]})

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

;; MANAGE ENTRIES

(defn get-entry [user source-name]
  (let [entries (user :entries)]
    (filter
     #(re-matches (re-pattern source-name) (:source %))
     entries)))

;; AUTHENTICATION

(defn authenticate []
  (* 1 1))

(defn authenticated? []
  (* 1 1))
