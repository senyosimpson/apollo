(ns apollo.core
  (:import [javax.sound.midi MidiSystem]))

(defn strToInt
  "Parses a string into an integer"
  [val]
  (Integer/parseInt val))

(defn play-note
  "Plays a note"
  [channel note]
  (println "Playing a note")
  (do 
    (.noteOn channel note 100)
    (Thread/sleep 1000)
    (.noteOff channel note)
  )
)
(defn get-synth
  "Gets a synth player"
  [channelNum instrumentNum]
  (with-open [synth (doto (MidiSystem/getSynthesizer) .open)]
    (do
      (let [channel (aget (.getChannels synth) channelNum)
            instrument (aget (.getAvailableInstruments synth) instrumentNum)]
        (println (str "Instrument is a " (.getName instrument)))
        (.loadInstrument synth instrument)
        (.programChange channel instrumentNum)
        channel))))

(defn -main
  "Get a synthesizer object"
  [channelNum instrumentNum]
  (let [synth (get-synth (strToInt channelNum) (strToInt instrumentNum))]
    (play-note synth 60)))
