(ns cursor.core
  (:require [cursor.platform :as platform]))


(defn virtual-cursor [val write! ref]
  (platform/->Cursor val write! [] (hash ref)))


(defn cursor [store]
  (virtual-cursor @store (fn [f] (swap! store f)) store))
