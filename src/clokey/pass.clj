(ns clokey.
  (:require [crypto.password.bcrypt :as password]
            [clojure.string :as string]))

;; CRUD

(defn create-password [pw] (encrypt pw))

(defn read-password []
  (* 1 1))

(defn update-password []
  (* 1 1))

(defn delete-passward []
  (* 1 1))

;; VALIDATION

(defn valid? []
  (* 1 1))

;; ENCRYPTION

(defn encrypt [pw]
  (password/encrypt pw))

(defn decrypt []
  (* 1 1))
