(ns cursor.core
  (:require [cursor.cursor :refer [->Cursor]]
            [cursor.ref-cursor :refer [->RefCursor]]))


(defn virtual-cursor [val write! ref]
  (->Cursor val write! [] (hash ref)))

(defn virtual-ref-cursor [deref! write! ref]
  (->RefCursor deref! write! [] (hash ref)))


(defn cursor [store]
  (virtual-cursor (deref store) (partial swap! store) store))

(defn ref-cursor [store]
  (virtual-ref-cursor (partial deref store) (partial swap! store) store))
