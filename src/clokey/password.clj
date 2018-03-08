(ns clokey.password
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [cheshire.core :refer :all]
            [clokey.utils :as utils]))

; <editor-fold> --------PASSWORD ACCESS-----------

;</editor-fold>

; <editor-fold> --------PASSWORD GENERATION -----------

(def all-chars-range (range 33 127))

(def selected-range  (concat (range 33 45)
                             (range 46 127)
                             (range 65 91) ; a-z and A-Z are included twice in order
                             (range 97 123)))

(def easy-range      (concat (range 48 58)
                             (range 97 123)
                             (range 65 91)
                             (range 33 39)
                             '(42 43 63 64)))

(defn get-character-sequence [& ranges]
  (map char (apply concat ranges)))

(defn generate-password
  "Generates a password in the pattern of XXX-XXX-XXX-XXX"
  ([] (generate-password "4" easy-range))
  ([number-of-blocks range]
   (loop [blocks []
          number-of-blocks (read-string number-of-blocks)]
     (if (>= (count blocks) number-of-blocks)
       (clojure.string/join "-" blocks)
       (recur
        (into blocks
              (vector (apply str
                        (take 3 (repeatedly #(rand-nth (get-character-sequence range)))))))
        number-of-blocks)))))

(defn valid? [pw]
  "Checks whether a password is sufficiently secure. The password must include:
   - minimum length of 10 characters
   - at least one lower case letter
   - at least one upper case letter
   - at least one digit
   - at least one special character: - @ # $ % ^ & + ="
  (let [pattern
        #"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-@#$%^&+=_])(?=\S+$).{10,30}$"]
    (and
     (string? pw)
     (re-matches pattern pw)
     true))) ; and returns the last value it evaluates if all are true (maybe cast to Boolean instead?)

;</editor-fold>
