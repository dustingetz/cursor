(ns cursor.root-at)


(defn get-in' [m ks invalid? not-found]
  (reduce (fn [m k]
            (let [v (get m k not-found)]
                  ;; we passed the contains check e.g. (contains? {:a nil} :a) => true,
                  ;; but maybe we want a nil value to count as not-found.
              (if (invalid? v) not-found v)))
          m
          ks))


(defn update-in' [invalid? not-found m ks f & args]
  ;; Always apply f with value the user sees, never care about what's in the
  ;; store, only care about the val that accounts for not-found resolution
  ;; (You don't want to inc the nil, you want to inc the not-found)
  (let [v (get-in' m ks invalid? not-found)
        m (update-in m ks (constantly v))]
    (apply update-in m ks f args)))


(defn root-at [ks f invalid? not-found]
  (if (seq ks)
    #(update-in' invalid? not-found % ks f)
    #(f (if (invalid? %) not-found %))))
