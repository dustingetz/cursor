(ns cursor.root-at)


(defn root-at [segments f]
  (if (seq segments)
    #(update-in % segments f)
    f))


(comment
  (= (root-at [] inc) inc))
