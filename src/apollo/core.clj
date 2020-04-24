(ns apollo.core
  (:import [javax.sound.midi MidiSystem]))

(defn strToInt
  "Parses a string into an integer"
  [val]
  (Integer/parseInt val))

(defn getSynth
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
  (getSynth (strToInt channelNum) (strToInt instrumentNum)))
