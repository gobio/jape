# Java Application Performance Explained

## Why use Jape?
Jape can help you to understand and optimize the performance of complicated processes visualizing execution time and dependencies between process's stages. In modern Java application which uses 
asynchronous computing with tools like CompletableFuture, parallel streams or RXJava it's sometimes tricky to see which parts are executed sequentially and which parallelly and what stages should be optimized to optimize the whole process.

## Example

Following examples show how delegating steps to separate threads can increase concurrency.
#### Sequential stream:
```
Observable.range(0,5)
          .map(this::map)
          .filter(this::filter)
          .scan(this::sum)
          .subscribe(this::subscribe);
```
![Sequential stream](http://gobio.eu/jape/media/sequential-stream.png)

#### Concurrent stream:
```
Observable.range(0,5)
          .observeOn(Schedulers.computation())
          .map(this::map)
          .filter(this::filter)
          .scan(this::sum)
          .subscribe(this::subscribe);
```

![Concurrent stream](http://gobio.eu/jape/media/concurrent-stream-1.png)

#### Concurrent stream 2
```
Observable.range(0,5)
          .observeOn(Schedulers.computation())
          .map(this::map)
          .observeOn(Schedulers.computation())
          .filter(this::filter)
          .scan(this::sum)
          .subscribe(this::subscribe);
```
![Concurrent stream 2](http://gobio.eu/jape/media/concurrent-stream-2.png)

#### Concurrent stream 3
```
Observable.range(0,5)
          .observeOn(Schedulers.computation())
          .map(this::map)
          .observeOn(Schedulers.computation())
          .filter(this::filter)
          .observeOn(Schedulers.computation())
          .scan(this::sum)
          .subscribe(this::subscribe);
```
![Concurrent stream 2](http://gobio.eu/jape/media/concurrent-stream-3.png)

#### Concurrent stream 4
```
Observable.range(0,5)
          .observeOn(Schedulers.computation())
          .map(this::map)
          .observeOn(Schedulers.computation())
          .filter(this::filter)
          .observeOn(Schedulers.computation())
          .scan(this::sum)
          .observeOn(Schedulers.computation())
          .subscribe(this::subscribe);
```
![Concurrent stream 2](http://gobio.eu/jape/media/concurrent-stream-4.png)

[Source](https://github.com/gobio/jape-example)

## Running example
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
a browser. Wait a while to finish some computation and click selected trace to
show the chart:
![Example](http://gobio.eu/jape/media/jape-1_1-example.png)
[Live example](http://gobio.eu/jape/example)

## Warning
The project is at the early experimental stage and should not be used in a production environment.