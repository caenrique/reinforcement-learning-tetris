# Introduccion

Hemos desarrollado un jugador de tetris que aprende a jugar utilizando una técnica de aprendizaje por refuerzo. En nuestro caso hemos escogido el algoritmo llamado QLearning. También hemos empleado técnicas heurísticas para ayudar a guiar el entrenamiento, ya que las caracteristicas del problema hacen que sea muy dificil comenzar a aprender.
Los resultados obtenidos mejoran el comportamiento de un jugador aleatorio, pero no por mucho. A continuación vamos a detallar cómo hemos desarrollado el proyecto, las decisiones que hemos tomado y qué análisis hacemos de los resultados, explicando las posibles causas de la falta de aprendizaje significativo.

# Cómo poner a funcionar el proyecto

## Cómo compilar el proyecto

Es necesario tener instalado java 8 y sbt > 1.0 para poder compilar el proyecto. Alternativamente, se puede importar el proyecto a un IDE que tenga integración con sbt como es IntelliJ Idea

Para compilar, tendremos que ejecutar el siguiente comando en la raiz del proyecto:

```
$ sbt assembly
```

Esto creará un jar en el directorio `jars/` dentro de la raiz del proyecto

### Versiones

Existen varias versiones del proyecto, que se discutirán más adelante. Para compilar cada una de estas versiones hace falta usar git.

```
$ git checkout branch-name
$ sbt assembly
```


## Cómo ejecutar el proyecto

Para ejecutar el proyecto podemos usar uno de los dos scripts de ayuda que están en el directorio `scripts/` en la raiz del proyecto. Hay un script para ejecutar el proyecto como jugador, y otro para entrenarlo. Las diferentes opciones de cada uno son:

1. Jugador:

```
$ jugar.sh jar-file episodes qfunction-file
```

2. Entrenador:

```
$ entrenar.sh jar-file episodes alpha gamma qfunction-file
$ entrenar.sh jar-file episodes qfunction-file
``` 

- alpha y gamma son números reales, mientras que episodes es un número entero
- jar-file es la ruta al jar que queremos ejecutar
- qfuncion-file es la ruta al archivo de la funcion q que queremos usar, o el nombre que se le pondrá si no existe

Otra manera de ejecutar el proyecto, sería llamando directamente al jar mediante `java -jar` y pasándole sus parámetros. Los parámetros que acepta están debidamente documentados en su `usage information` que será mostrada si los parámetros no son correctos o si se llama sin parámetros.

Es necesario que el servidor esté funcionando antes de la ejecución del jugador para que este se pueda conectar adecuadamente

# Descripción del proyecto

Para poder entender cómo está funcionando el proyecto, vamos a dar una descripción esquemática de los diferentes componentes lógicos del proyecto

## Representación del tablero

Durante el desarrollo hemos probado con diferentes representaciones del tablero, y no hemos conseguido que ninguna de ellas sea significativamente mejor que el resto. Por eso, para mostrar los resultados, hemos estructurado el proyecto de forma que se pueda acceder a las tres versiones distintas. Estas versiones se encuentran almacenadas en tres branches o ramas diferentes de git, y por lo tanto, para poder tener acceso a todas, necesitamos usar dicho software de versiones.

Las representaciones de las que estamos hablado corresponden con la clase `SimpleBoard` y son las usadas para almacenar el estado en la tabla q, pero no para procesar toda la logica del jugador. Para eso el tablero se representa como un vector de dos dimensiones, tal y como viene desde el servidor. Esta representación corresponde con la clase `Board`

Las tres representaciones del tablero son las siguientes:

1. Vector de altura de columnas
2. Vector de booleanos
3. Vector de características

Tanto la representacion 1 como la 3 están basadas en los resultados obtenidos en el articulo \ref{1}

### Vector de altura de columnas

Esta representación es muy sencilla de implementar, y a primera vista, parece que puede dar buenos resultados. Supone ignorar información del tablero, como son los huecos.

### Vector de booleanos

Se calcula un vector de booleanos a partir de el tablero actual y la ficha que está callendo, con una rotacion determinada. El vector tiene tantas posiciones como posibles desplazamientos laterales tenga la ficha. Para cada par (tablero, ficha) existen 4 vectores diferentes, correspondientes con las 4 rotaciones de la ficha.

### Vector de características

Está compuesto por características calculadas a partir del tablero (`Board`) teniendo conocimento de fondo del problema. En nuestro caso, las características que lo componen son dos:
1. El número de huecos
2. La altura máxima

## Representación de la función Q

La representación de la función Q está ligada a la representación del tablero. Básicamente se trata de un mapeo de estado (o estado-accion) a valor. En todos los casos el valor siempre es un número real. La clave en cada uno de los casos viene dada por:

1. Vector de alturas: clave -> (tablero, figura, desplazamiento lateral, rotacion)
2. Vector de booleanos: clave -> (tablero, desplazamiento lateral)
3. Vector de caracteristicas: clave -> tablero

por lo tanto, en algunos casos estaremos utilizando una función Q que mapea estado-accion a valores, y en otros casos estados a valores.

## Simulación de juego

Para poder valorar qué movimientos son mejores, el tablero es capaz de calcular cuál sería el siguiente tablero dada una figura, un desplazamiento y una rotación. Esto lo utilizamos para aplicarle después una heurística (o la función Q) y poder valorar qué acciones nos llevan a un mejor desenlace, un o dos movimientos vista.

## Entrenador

La clase `Trainer` se encarga de gestionar todo lo relacionado con el algoritmo de aprendizaje y la actualización de la tabla Q

# Proceso de entrenamiento

## Parámetros del algoritmo

Los parámetros que tiene nuestro algoritmo son alpha y gamma. Alpha es el factor de aprendizaje y gamma regula cuanto tiene en cuenta el valor de los estados futuros.
A la hora de experimentar qué funcionaba y que no, probamos con establecer un alpha variable, que fuera decreciendo con el tiempo, de forma que facilitara la convergencia. Esto no dio señales de mejorar.
Otra técnica que probamos fue la reinicialización de alpha al valor inicial después de n episodios, de forma cíclica, tal y como se explica en \ref{2}(reinforcement learning. An introduction). Esto tampoco dio señales de suponer una mejora.

## Estrategia o Policy

Una estategia o policy en nuestro proyecto es una función que mapea una lista de Acciones y sus correspondientes evaluaciones según la función Q y una heurística `(accion, Qvalue, Hvalue)` a una acción concreta. Escoje la mejor acción según cierto criterio.

Estamos usando una estategia e-greedy modificada para incluir un heurístico, de forma que tenemos 3 posibles formas de determinar qué acción es la que vamos a escoger: aleatoria, la mejor según la tabla Q o la mejor según la heurística

Una técnica que también probamos es que se fuera reduciendo con el tiempo la probabilidad de escojer una acción de forma aleatoria. No vimos cambios significativos en los resultados.

## Gamma y calculo del "potencial" o el mejor valor de las posibles acciones siguientes

El algoritmo Q-learning es un algoritmo off-policy, lo que quiere decir que no utiliza la estategia para calcular cual es el mejor valor posible de las acciones futuras, a la hora de actualizar la función Q.
En el caso de otros algoritmos como SARSA, el cálculo de este "potencial" se hace siguiendo la estrategia, de forma que entrena tal y como después se seleccionarían los movimientos, pero en nuestro caso, la cantidad de estados y los pocos estados buenos que tenemos, nos llevan a pensar que una técnica off-policy sería más adecuada.

El cálculo de este "potencial" se hace de una forma un poco diferente según la versión de la representación del tablero, pero básicamente lo que hacen todas es utilizar la figura que está callendo, y la figura siguiente para hacer una búsqueda de profundidad 2 y obtener los valores que da la tabla Q de todos los estados a los que se llegue, para así quedarse con el mejor.

# Resultados

Los resultados obtenidos no son buenos. No hemos conseguido que aprenda de forma significatica, aunque mejora la puntuación de un jugador aleatorio. Los valores de alpha y gamma que hemos usado han sido 0.2 para alpha y 0.4 para gamma. A lo largo de todo el desarrollo hemos ido probando diferentes valores, así como diferentes técnicas (como ya hemos explicado), y esa combinación de valores es la que parece dar mejores resultados, aunque no hemos podido hacer una prueba más rigurosa en este sentido.

Para medir el resultado de cada jugador hemos utilizado dos características empleadas durante el entrenamiento y durante el juego:

1. media de movimientos por partida y
2. media de lineas completadas por partida

Podemos ver los resultados de cada versión en la siguiente tabla

Meter aqui tabla de resultados

Podemos ver que todas las versiones mejoran al jugador aleatorio, aunque no por mucho, y que la que mejor resultados ha obtenido es VERSION


# Posibles problemas

1. Rango de valores de inicialización muy distinto a los valores de la tabla una vez se empiezan a actualizar
2. Posibilidad de que esté fluctuando por un alpha muy alto
3. La representación del estado no permite generalizar sufiente compo para que pueda aprender
4. La representación del estado engloba demasiados estados distintos en una misma representación, por lo que algunos buenos mejoran el valor cuando se actualiza, y otros malos lo empeoran. Esto puede estar causando que el valor no se estabilice
5. La recompensa tiene un rango de valores de 100 a 800, y esto puede ser un problema por la diferencia con los valores inicialeso

# Posibles mejoras

1. En algunos casos, el buen o mal funcionamiento del jugador en el aprendizaje depende de los valores iniciales de la tabla Q, y hemos podido observar que en algunas ejecuciones comenzaba a jugar bastante bien, por lo que una posible mejora sería un sistema de entrenamientos cortos, en los que se guardara la función Q resultante de aquellos con mejor puntuación, y después se continuara entrenando por ahí.

2. Quizá utilizando una algoritmo más complejo serviría para mejorar el resultado. Se podría probar con otro tipo de aprendizaje por refuerzo como SARSA o como Double-Q-learning

3. Escoger la representación del tablero más acertada es quizá una de las tarea más dificiles. Probablemente haya una representación que de mejores resultados

4. Utilizar algún tipo de procesamiento sobre la recompensa. Posiblemente el preprocesar los valores de recompensa antes de meterlos en el algoritmo permita que el funcionamiento sea más adecuado

5. Utilizar la heurística para guiar los primeros movimientos de cada partida, de forma que aprenda partiendo de una partida con algunas líneas ya hechas y evitar así la dificultad más grande del aprendizaje, que es la inmensa cantidad de opciones equivocadas que tiene el espacio de estados
