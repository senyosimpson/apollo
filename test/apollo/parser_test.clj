(ns apollo.parser-test
  (:require [clojure.test :refer :all]
            [apollo.parser :refer :all]))


(deftest get-instrument-number-test
  (is (= 0 (get-instrument-number "Acoustic Grand Piano")))
  (is (nil? (get-instrument-number "Grand Piano"))))


(deftest get-instrument-name-test
  (is (= "Piano" (get-instrument-name "Piano: o4"))))


(deftest note-offsets-test
  (let [note-offsets (get-note-offsets ["c" "c#" "d"])
        correct {"c" 0 "c#" 1 "d" 2}
        incorrect {"c" 0 "c#" 2 "d" 1}]
  (is (= note-offsets correct))
  (is (not= note-offsets incorrect))))


; should use a fixture here
(deftest get-score-metadata-test
  (let [score (slurp "test/apollo/testscore.apl")]
    (is (= (get-score-metadata score) "Acoustic Grand Piano: o4"))))


; should use a fixture here
(deftest get-score-notes-test
  (let [score (slurp "test/apollo/testscore.apl")]
    (is (= (get-score-notes score) "c d e f g a b c c b a g f e d c"))))


(deftest get-octave-base-note-test
  (is (= (get-octave-base-note 4) 60))
  (is (= (get-octave-base-note) 60)))


(deftest get-score-octave-test
  (let [score-info (get-score-metadata (slurp "test/apollo/testscore.apl"))]
    (is (= (get-score-octave score-info) 4))))


(deftest to-midi-note-test
  (is (= (to-midi-note "c" 4) 60))
  (is (= (to-midi-note "d" 4) 62)))


(deftest get-note-duration-test
  (is (= (get-note-duration "c" true) 1))
  (is (= (get-note-duration "c4") 4))
  (is (nil? (get-note-duration "c"))))


(deftest get-note-letter-test
  (is (= (get-note-letter "c") "c")
  (is (= (get-note-letter "c4") "c"))))


(deftest get-valid-duration-test
  (is (= (get-valid-duration 4 nil) 4))
  (is (= (get-valid-duration 4 2) 2)))
