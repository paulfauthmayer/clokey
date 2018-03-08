(ns clokey.utils
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [cheshire.core :refer :all]
            [buddy.hashers :as hashers]))


; <editor-fold> ---------ENCRYPTION---------------------

(defn encrypt [pw]
   (hashers/derive pw))

(defn decrypt [pw hash]
  (hashers/check pw hash))

(defn get-entry-data [username entry])

; </editor-fold>



; <editor-fold> --------FILESYSTEM READ/WRITE -----------

(defn get-path
  "path to the userdata folder on the filesystem, configuration value"
  [user]
  (str "./data/" user ".txt"))

(defn exists?
  "Checks if a file already exists"
  [user]
  (true?
   (.exists (io/file (get-path user)))))

(defn create-user-file
  "Creates a file with a given username, e.g. paul.txt"
  [name]
  (with-open [wrtr (io/writer (get-path name))]))

(defn read-file
  "Read data from a file and return as string, parses from json"
  [user]
  (if
    (exists? user)
    (parse-string (slurp (get-path user)) true)
    (println "File does not exist!")))

(defn write-to-file
  "Write a given input to a file, will create a file if none exists, parses to json"
  [user, input]
  (if (exists? user)
    (spit (get-path user) (generate-string input))
    (do
     (println (str "File does not exist, creating file " user ".txt"))
     (create-user-file user)
     (spit (get-path user) (generate-string input)))))

; </editor-fold>
