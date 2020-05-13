(ns apollo.midi
  (:import (javax.sound.midi ShortMessage MidiEvent)))

(defn note-offsets [notes] 
  (loop [mapping {}
         iteration 0
         notes notes]
    (if (empty? notes)
      mapping
      (recur (into mapping (hash-map (first notes) iteration))
             (inc iteration)
             (rest notes)))))

(defn note-to-midi [base note-offsets-map note]
  "Converts a note to a midi number. For example, middle c -> 60"
  (+ base (get note-offsets-map note)))

(defn get-octave-base-note
  "Gets the first midi note number for an octave"
  ([octave]
    (+ 24 (* 12 (- octave 1))))
  ([]
    (get-octave-base-note 4)))

(defn create-midi-event [message tick]
  (new MidiEvent message tick))

(defn create-note-on-event [note volume tick]
  "Creates a midi note on message"
  (create-midi-event 
    (doto (new ShortMessage) (.setMessage (ShortMessage/NOTE_ON 0 note volume)))
    tick))

(defn create-note-off-event [note volume tick]
  "Creates a midi note off message"
  (create-midi-event
    (doto (new ShortMessage) (.setMessage (ShortMessage/NOTE_OFF 0 note volume)))
    tick))


(defn add-track-events [track note volume tick]
  (do
    (doto track (.add (create-note-on-event note volume tick)))
    (doto track (.add (create-note-off-event note volume tick)))
    track)) ; add some logic for the ticks