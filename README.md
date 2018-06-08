# Java Application Performance Explained

##Why use Jape?
Jape can help you to understand and optimize the performance of complicated processes visualizing execution time and dependencies between process's stages. In modern Java application which uses 
asynchronous computing with tools like CompletableFuture, parallel streams or RXJava it's sometimes tricky to see which parts are executed sequentially and which parallelly and what stages should be optimized to optimize the whole process.

##Example
In the following example, you can see the synchronous process:
![Synchronous example](http://gobio.eu/jape/media/jape_synchronous_example.png)
The diagram shows that **compute** stage lasts about 9s and consists of stages 
**fetchData**, **makeComputation** and **notifyListeners**. Whereas **makeComputation**
consists of sequentially executed stages **expensiveComputation** and **reduction**.
You can see source code [here](https://github.com/gobio/jape-example/blob/master/src/main/java/eu/gobio/jape/example/SyncComputation.java).
After an introduction of the operator [**observeOn**](http://reactivex.io/documentation/operators/observeon.html)
in **makeComputation** and **notifyListeners** methods 
([source code](https://github.com/gobio/jape-example/blob/master/src/main/java/eu/gobio/jape/example/AsyncComputation.java))
we can see, that **reduction** is executed **parallelly** with **expensiveComputation**,
and **asyncNotification** is executed after **computeAsync** method finishes.
![Asynchronous example](http://gobio.eu/jape/media/jape_asynchronous_example.png)
(generated with [Simple Explainer module](https://github.com/gobio/jape-simple-explainer))

## Getting started
Clone or download the [example project](https://github.com/gobio/jape-example)
and execute command:  
*Linux*
```
./gradlew run
```
*Windows*
```
gradlew.bat run
```
The program will start example computation and open url http://localhost:5005 in
your browser. Wait a while to finish some computation and click selected trace to
show the chart:
    ![Example](http://gobio.eu/jape/media/jape_example.png)

##Warning
The project is at the early experimental stage and should not be used in a production environment.