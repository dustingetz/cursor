(ns cursor.core
  (:require [cursor.cursor :refer [->Cursor]]))


(defn virtual-cursor [val write! ref]
  (->Cursor (constantly val) write! [] (hash ref) true))

(defn virtual-ref-cursor [deref write! ref]
  (->Cursor deref write! [] (hash ref) false))


(defn cursor [store]
  (virtual-cursor (deref store) (partial swap! store) store))

(defn ref-cursor [store]
  (virtual-ref-cursor (partial deref store) (partial swap! store) store))
