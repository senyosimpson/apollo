(ns apollo.midi
  (:import (javax.sound.midi ShortMessage MidiEvent MidiSystem Sequence))
  (:require [clojure.java.io :as io]))

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
    (doto (new ShortMessage) (.setMessage ShortMessage/NOTE_ON 0 note volume))
    tick))

(defn create-note-off-event [note volume tick]
  "Creates a midi note off message"
  (create-midi-event
    (doto (new ShortMessage) (.setMessage ShortMessage/NOTE_OFF 0 note volume))
    tick))

(defn add-track-events [track note volume tick]
  (do
    (doto track (.add (create-note-on-event note volume tick)))
    (doto track (.add (create-note-off-event note volume (+ 250 tick)))))
    track)

(defn set-instrument-channel [synth instrument-num channel-num]
  "Sets a channel to the specified instrument"
  (let [channel (aget (.getChannels synth) channel-num)
        instrument (aget (.getAvailableInstruments synth) instrument-num)]
    (println (str "Setting instrument " (.getName instrument) " on channel " channel-num))
    (.loadInstrument synth instrument)
    (.programChange channel instrument-num)))

(defn set-sequence [sequencer notes]
  (let [sequence (new Sequence Sequence/PPQ 1)]
    (let [track (.createTrack sequence)]
      (doseq [[note tick]
              (map list notes (for [tick (range (count notes))] (* 250 tick)))]
        (add-track-events track note 60 tick))
    (doto sequencer (.setSequence sequence)))
    sequencer))

(defn get-sequencer [instrument-nums]
  (let [synth (doto (MidiSystem/getSynthesizer) .open)
        sequencer (doto (MidiSystem/getSequencer) .open)]
    (doseq [[instrument-num channel-num]
            (map list instrument-nums (range (count instrument-nums)))]
      (set-instrument-channel synth instrument-num channel-num))
    (let [sequencerTransmitter (.getTransmitter sequencer)
          synthesizerReceiver (.getReceiver synth)]
      (.setReceiver sequencerTransmitter synthesizerReceiver))
    sequencer))

(defn play [instrument-nums notes]
  (let [sequencer (get-sequencer instrument-nums)]
    (set-sequence sequencer notes)
    (.start sequencer)
    (while (true? (.isRunning sequencer)))
    (.close sequencer)))