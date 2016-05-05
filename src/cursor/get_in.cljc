(ns cursor.get-in
  (:require [cursor.mk-sentinel :refer [mk-sentinel]]))


(defn get-in' [m ks not-found get]
  (loop [sentinel (mk-sentinel)
         m m
         ks (seq ks)]
    (if ks
      (let [m (get m (first ks) sentinel)]
        (if (identical? sentinel m)
          not-found
          (recur sentinel m (next ks))))
      m)))
