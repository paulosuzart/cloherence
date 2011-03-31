(ns cloherence.core
	(:use [clojure.contrib.def :only [name-with-attributes]])
	(:import [com.tangosol.net CacheFactory NamedCache]
			 [com.tangosol.util.processor AbstractProcessor]))


	(defprotocol PProcessor 
		(process [this entry]))

	(defn join-grid
	  "Helper function to ensure cluster"	
	  []
	  (do
	    (CacheFactory/ensureCluster)))

	(defn left-grid 
	  "Gracefuly shut down the grid"
	  []
	  (CacheFactory/shutdown))
	
	(def *cache* nil)
	
	(defn get-cache 
		"Get a NamedCache reference using CacheFactory"
		[name]
		(CacheFactory/getCache name))

	(defn get-val 
		"get entry from cache"
		[e-key]
		(.get *cache* e-key))
	
	(defn put-val
		"Put an entry to cache, returns the old value if any"
		[e-key e-value]
			(.put *cache* e-key e-value))	

	(defn remove-val 
		"Remove a entry from cache keyed with e-key. Note: This is
		an optional implementation for some caches"
		[e-key]
		(.remove *cache* e-key))

	(defn put-seq 
		"Put every item from 'a' (say, i) using (f i) as key. Returns nil."
		[f coll]
		(.putAll *cache* (zipmap (map #(f %) coll) coll)))

	(defn make-processor 
		"Create a valid Coherence EntryProcessor from the PProcessor passed 
		 as agurment"
		[processor] 
		(proxy [AbstractProcessor] []
 		  (process [entry]
				(process processor entry))))

 	(defn process 
		"In the scope of a with-cache calls a processor built by make-processor againts
		 the current cache"
		[e-key processor]
		(.invoke *cache* e-key (make-processor processor)))

	(defmacro with-cache [cache-name & body]
		`(binding [*cache* (get-cache ~cache-name)]
			~@body))
	
	(defmacro cache-map 
		[name & kvs]
    	(let [[name kvs] (name-with-attributes name kvs)]
			`(do
      			(def ~name ~(str name)))))
	
	(defn assoc
		([cache e-key e-val]
			(with-cache cache	
				(put-val e-key e-val)))
		([cache e-key e-val & kvs]
				(.putAll (get-cache cache) 
					(loop [result {} coll (cons e-key (cons e-val kvs))]
						(if (empty? coll)
							result
							(recur 
								(merge result {(first coll) (second coll)}) (nnext coll)))))))
	(defn count
		[cache]
		(.size (get-cache cache)))
	
	(defn get
		[cache e-key]
		(with-cache cache (get-val e-key)))
	
