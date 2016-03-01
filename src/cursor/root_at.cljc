(ns cursor.root-at)


(defn root-at [segments f] #(update-in % segments f))
