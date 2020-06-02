# Consumer delivery semantics

## At most once


Los offsets se comitean tan pronto como el batch de mensajes se recibe. Si el proceso no funcional, el mensaje se perderá (no va a ser leído de nuevo.)


1- Lee el batch.
2- Comitea el offset
3- Procesa los datos (se puede romper)
4- Lee con el offset comiteado.

## At least once

Los offsets son comiteados tras procesar el mensaje, si algo va mal, el mensaje se leerá de nuevo. El proceso ha de ser **idempotente**

## Exactly once

Sólo con Kafka, Kafka workflows con la Streams api

## recomendación

at least once con operaciones idempotentes


# Idempotencia

2 estrategias para elastic:

kafka generic id

```java
 String id = record.topic() + "_" + record.partition() + "_" + record.offset();
 ```
Hay que indicar el id: 
```java
request = new IndexRequest(record.topic()).id(id).source(result.toString(), XContentType.JSON);
```

# Consumer poll behaviour
Kafka consumers tienen un modelo de poll, mientras muchos otros message brokers funcionan con push.
Esto permite a los consumidores controlas donde el el log quieren consumir y les proporciona la habilidad para repetir eventos.


 otra estrategia es generar un id desde el registros, que ha de ser único.

 fetch.min.bytes (default 1)
 - Controla cuantos datos quieres hacer pull al menos en cada request
 - ayuda a reducir el numero de peticiones

max.poll.records (default 500)
- controla cuantos registros quieres recibir en cada petición 
- se incrementa si tus mensajes son muy pequeños y tienes mucha RAM
- bueno es monitorizar cuantos registros son poll por petición

max.partitions.fetch.bytes (default 1mb)
- máximo data devuelta por el broker por cada particion
- si tienes muchas particiones, ten mucha ram!

fetch.max.bytes (default 50mb)
- máximos datos devueltos por cada fetch reques (cubre muchas particiones)
- el consumidor realiza muchas fetch en paralelo

# Consumer offsets commit strategies

dos patrones generales:

- (easy) enable.auto.commit = true & synchronous processing of batches
- (medium) enable.auto.commit = false y commit manual.

#### enable.auto-commit true
con auto-commit los offsets se comitearán automáticamente a intervalos regulrares 
(auto.commit.interval.ms = 5000 por defecto) cada vez que llamas a poll

commit manual:
```java
consumer.commitSync();
```

# Volver al curso y comitear manualmente.



