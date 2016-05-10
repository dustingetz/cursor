(ns cursor.core
  (:require [cursor.platform :as platform]))


(defn cursor [store] (platform/cursor store))
  ;; Because of platform differences, this needs to be abstracted into a
  ;; pure clojure function, per our understanding.
