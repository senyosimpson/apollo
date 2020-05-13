(ns apollo.audio
  (:import (javax.sound.midi MidiSystem))
  (:require [clojure.java.io :as io]))

(defn set-instrument-channel [synth instrument-num channel-num]
  "Sets a channel to the specified instrument"
  (let [channel (aget (.getChannels synth) channel-num)
        instrument (aget (.getAvailableInstruments synth) instrument-num)]
    (println (str "Setting instrument " (.getName instrument) " on channel " channel-num))
    (.loadInstrument synth instrument)
    (.programChange channel instrument-num)))

(defn set-sequence [sequencer midifile]
  (let [sequence (MidiSystem/getSequence (io/file midifile))]
    (doto sequencer (.setSequence sequence))))

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

(defn play [instrument-nums midifile]
  (let [sequencer (get-sequencer instrument-nums)]
    (set-sequence sequencer midifile)
    (.start sequencer)
    (while (true? (.isRunning sequencer)))
    (.close sequencer)))
