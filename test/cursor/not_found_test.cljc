#?(:clj  (ns cursor.not-found-test
           (:require [clojure.test :refer [deftest testing is]]
                     [cursor.core :refer [cursor]]))
   :cljs (ns cursor.not-found-test
           (:require-macros [cljs.test :refer [deftest testing is]])
           (:require [cljs.test]
                     [cursor.core :refer [cursor]])))


(deftest not-found-multiple-places []
  (let [store (atom {})
        cur (cursor store)]
    (swap! ((cur [:a :b] {:c 10}) [:c]) inc)
    (is (= {:C 10} @(cur [:a :B] {:C 10})))))


(deftest not-found-multiple-places2 []
  (let [store (atom {})
        cur (cursor store)
        common (cur [:common] {:b {:c 10}
                               :x {:y 20}})
        b (common [:b])
        x (common [:x])]

    (swap! (b [:c]) inc)
    (is (= {:b {:c 11}
            :x {:y 20}}
           (get-in @store [:common])))

    ; shouldComponentUpdate skips re-rendering c-y since nothing changed, so use old reference
    (swap! (x [:y]) inc)
    (is (= {:b {:c 11}
            :x {:y 21}}
           (get-in @store [:common])))))
