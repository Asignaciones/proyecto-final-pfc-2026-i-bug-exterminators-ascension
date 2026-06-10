# Informe de Corrección
 
---

## 2.3 Función `solapan`

### Especificación

Sea $f : \text{Curso} \times \text{Curso} \to \text{Boolean}$ la función que determina si dos cursos se solapan en el tiempo. Dados dos cursos $c_1$ y $c_2$ con intervalos $[\text{ini}_1, \text{fin}_1)$ y $[\text{ini}_2, \text{fin}_2)$, la especificación matemática es:

$$
f(c_1, c_2) = \text{ini}_1 < \text{fin}_2 \;\land\; \text{ini}_2 < \text{fin}_1
$$

### Implementación

```scala
def solapan(c1: Curso, c2: Curso): Boolean = {
  val inicio1 = iniCurso(c1)
  val inicio2 = iniCurso(c2)
  val final1  = finCurso(c1)
  val final2  = finCurso(c2)
  inicio1 < final2 && inicio2 < final1
}
```

### Argumentación de corrección

Esta función no es recursiva, por lo tanto se argumenta su corrección directamente evaluando la expresión retornada.

Queremos demostrar que:

$$
\forall c_1, c_2 \in \text{Curso} : P_f(c_1, c_2) == f(c_1, c_2)
$$

**Demostración:**

Las funciones de acceso `iniCurso` y `finCurso` extraen exactamente los campos correspondientes de cada curso, por lo tanto:

$$
\text{inicio1} = \text{ini}_{c_1}, \quad \text{inicio2} = \text{ini}_{c_2}, \quad \text{final1} = \text{fin}_{c_1}, \quad \text{final2} = \text{fin}_{c_2}
$$

La expresión retornada es:

$$
P_f(c_1, c_2) \to \text{inicio1} < \text{final2} \;\land\; \text{inicio2} < \text{final1}
$$

Sustituyendo:

$$
\to \text{ini}_{c_1} < \text{fin}_{c_2} \;\land\; \text{ini}_{c_2} < \text{fin}_{c_1}
$$

Que es exactamente $f(c_1, c_2)$.

**Conclusión:**

$$
\forall c_1, c_2 \in \text{Curso} : P_f(c_1, c_2) == f(c_1, c_2) \quad \checkmark
$$
 
---

## 2.4 Función `choques`

### Especificación

Sea $f : \text{Cursos} \times \text{Asignacion} \to \mathbb{N}$ la función que cuenta el número de pares $(i, j)$ con $i < j$ tales que $\alpha_i = \alpha_j \geq 0$ y los cursos $i$ y $j$ se solapan. Formalmente:

$$
f(\text{cursos}, \alpha) = \left|\{(i,j) \mid 0 \leq i < j < n,\; \alpha_i = \alpha_j \geq 0,\; \text{solapan}(c_i, c_j)\}\right|
$$

### Implementación

```scala
def choques(cursos: Cursos, a: Asignacion): Int = {
  val pares = for {
    i <- 0 until cursos.length
    j <- (i + 1) until cursos.length
    if a(i) >= 0 && a(j) >= 0
    if a(i) == a(j)
    if solapan(cursos(i), cursos(j))
  } yield 1
  pares.length
}
```

### Argumentación de corrección

Esta función tampoco es recursiva. Se argumenta su corrección mostrando que la `for`-comprehension genera exactamente el conjunto descrito en la especificación.

Queremos demostrar que:

$$
\forall \text{cursos} \in \text{Cursos},\; \alpha \in \text{Asignacion} : P_f(\text{cursos}, \alpha) == f(\text{cursos}, \alpha)
$$

**Demostración:**

La `for`-comprehension recorre todos los pares $(i, j)$ con $0 \leq i < j < n$, que corresponde exactamente al conjunto de índices válidos de la especificación. Para cada par aplica tres filtros:

1. $\alpha_i \geq 0 \;\land\; \alpha_j \geq 0$ — ambos cursos tienen aula asignada.
2. $\alpha_i = \alpha_j$ — los cursos comparten aula.
3. $\text{solapan}(c_i, c_j)$ — los cursos se solapan en tiempo.
   Un par $(i,j)$ produce un $1$ si y solo si cumple los tres filtros, lo cual coincide exactamente con la condición de la especificación. Por lo tanto:

$$
\text{pares} = \{1 \mid (i,j) \text{ cumple las tres condiciones}\}
$$

Y como la corrección de `solapan` ya fue demostrada:

$$
P_f(\text{cursos}, \alpha) = |\text{pares}| = f(\text{cursos}, \alpha)
$$

**Conclusión:**

$$
\forall \text{cursos}, \alpha : P_f(\text{cursos}, \alpha) == f(\text{cursos}, \alpha) \quad \checkmark
$$
 
---

## 2.5. Funcion `capacidadFallida`

### Especificacion

Sea $f : \text{Cursos} \times \text{Aulas} \times \text{Asignacion} \to \mathbb{N}$
la funcion que cuenta el numero de cursos cuya aula asignada no tiene
capacidad suficiente para sus estudiantes. Formalmente:

$$
f(\text{cursos}, \text{aulas}, \alpha) = \left|\{ i \mid 0 \leq i < n,\; \text{aulas}(\alpha_i)._2 < \text{cursos}(i)._4 \}\right|
$$

donde $n = |\text{cursos}|$, $\text{cursos}(i)._4$ es la cantidad de
estudiantes del curso $i$ y $\text{aulas}(\alpha_i)._2$ es la capacidad
del aula asignada al curso $i$.

### Implementacion

```scala
def capacidadFallida(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {
  val cursAuls = for {
    i <- (0 until cursos.length)
  } yield (cursos(i)._4, aulas(a(i))._2)
  cursAuls.count(p => p._2 < p._1)
}
```

### Argumentacion de correccion

Esta funcion no es recursiva. Se argumenta su correccion mostrando
que la `for`-comprehension construye exactamente el conjunto de tuplas
descrito en la especificacion y que `count` implementa la cardinalidad
del conjunto de fallos.

Queremos demostrar que:

$$
\forall \text{cursos} \in \text{Cursos},\; \text{aulas} \in \text{Aulas},\; \alpha \in \text{Asignacion} : P_f(\text{cursos}, \text{aulas}, \alpha) == f(\text{cursos}, \text{aulas}, \alpha)
$$

**Demostracion:**

La `for`-comprehension recorre todos los indices $i \in [0, n)$ y
para cada uno construye la tupla:

$$
(e_i, c_i) = (\text{cursos}(i)._4,\; \text{aulas}(\alpha_i)._2)
$$

donde $e_i$ es la cantidad de estudiantes del curso $i$ y $c_i$ es
la capacidad del aula asignada. El vector resultante es:

$$
\text{cursAuls} = \{(e_i, c_i) \mid 0 \leq i < n\}
$$

La operacion `count(p => p._2 < p._1)` cuenta exactamente las tuplas
donde $c_i < e_i$, es decir:

$$
P_f(\text{cursos}, \text{aulas}, \alpha) = |\{(e_i, c_i) \in \text{cursAuls} \mid c_i < e_i\}| = |\{i \mid c_i < e_i\}|
$$

Lo cual coincide exactamente con la definicion de $f$.

**Conclusion:**

$$
\forall \text{cursos},\; \text{aulas},\; \alpha : P_f(\text{cursos}, \text{aulas}, \alpha) == f(\text{cursos}, \text{aulas}, \alpha) \quad \checkmark
$$

---

## 2.5. Funcion `desperdicio`

### Especificacion

Sea $g : \text{Cursos} \times \text{Aulas} \times \text{Asignacion} \to \mathbb{N}$
la funcion que suma los puestos sobrantes de las aulas con capacidad
suficiente. Formalmente:

$$
g(\text{cursos}, \text{aulas}, \alpha) = \sum_{\substack{i=0 \\ \text{aulas}(\alpha_i)._2 \geq \text{cursos}(i)._4}}^{n-1} \left(\text{aulas}(\alpha_i)._2 - \text{cursos}(i)._4\right)
$$

Las aulas con capacidad insuficiente ($\text{aulas}(\alpha_i)._2 < \text{cursos}(i)._4$)
no contribuyen a la suma, ya que no existe desperdicio en ese caso.

### Implementacion

```scala
def desperdicio(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {
  val cursAuls = for {
    i <- (0 until cursos.length)
  } yield (cursos(i)._4, aulas(a(i))._2)
  val listaDesperdicio = cursAuls.toList.filter {
    case (cantEst, capacidadAuls) => capacidadAuls >= cantEst
  }.map {
    case (cantEst, capacidadAuls) => capacidadAuls - cantEst
  }
  listaDesperdicio.sum
}
```

### Argumentacion de correccion

Esta funcion no es recursiva. Se argumenta su correccion mostrando
que la cadena `filter` → `map` → `sum` implementa exactamente la
sumatoria condicional de la especificacion.

Queremos demostrar que:

$$
\forall \text{cursos} \in \text{Cursos},\; \text{aulas} \in \text{Aulas},\; \alpha \in \text{Asignacion} : P_g(\text{cursos}, \text{aulas}, \alpha) == g(\text{cursos}, \text{aulas}, \alpha)
$$

**Demostracion:**

**Paso 1 — construccion de `cursAuls`:**

La `for`-comprehension construye el mismo vector de tuplas que en
`capacidadFallida`:

$$
\text{cursAuls} = \{(e_i, c_i) \mid 0 \leq i < n\}
$$

donde $e_i = \text{cursos}(i)._4$ y $c_i = \text{aulas}(\alpha_i)._2$.

**Paso 2 — `filter`:**

El predicado `capacidadAuls >= cantEst` retiene exactamente las
tuplas donde $c_i \geq e_i$:

$$
\text{filtrado} = \{(e_i, c_i) \in \text{cursAuls} \mid c_i \geq e_i\}
$$

Esto corresponde a los cursos cuya aula tiene capacidad suficiente,
que son los unicos que generan desperdicio. Las tuplas con $c_i < e_i$
son descartadas correctamente.

**Paso 3 — `map`:**

La funcion `capacidadAuls - cantEst` calcula el desperdicio individual
de cada aula con capacidad suficiente:

$$
\text{listaDesperdicio} = \{c_i - e_i \mid (e_i, c_i) \in \text{filtrado}\}
$$

Dado que el `filter` garantiza $c_i \geq e_i$, todos los valores son
no negativos: $c_i - e_i \geq 0$.

**Paso 4 — `sum`:**

La suma de `listaDesperdicio` es:

$$
P_g = \sum_{(e_i, c_i) \in \text{filtrado}} (c_i - e_i) = \sum_{\substack{i=0 \\ c_i \geq e_i}}^{n-1} (c_i - e_i)
$$

Lo cual coincide exactamente con la definicion de $g$.

**Caso limite:** si todas las aulas tienen capacidad insuficiente,
`filtrado` es vacio y `listaDesperdicio.sum = 0`, correcto por
definicion de sumatoria sobre conjunto vacio. Si todas las aulas
tienen capacidad exacta ($c_i = e_i$), cada termino es $0$ y la
suma total es $0$. Ambos casos estan cubiertos por la prueba
**"capacidad exacta produce desperdicio 0"**. ✓

**Conclusion:**

$$
\forall \text{cursos},\; \text{aulas},\; \alpha : P_g(\text{cursos}, \text{aulas}, \alpha) == g(\text{cursos}, \text{aulas}, \alpha) \quad \checkmark
$$

---

## 2.8. Funcion `generarAsignaciones` (version secuencial)

### Especificacion

Sea $f : \mathbb{N} \times \mathbb{N} \to \text{Vector}[\text{Asignacion}]$
la funcion que genera todas las asignaciones posibles de $m$ aulas
para $n$ cursos. El conjunto de asignaciones es el producto cartesiano:

$$
f(n, m) = \{0, \ldots, m-1\}^n
$$

con $|f(n, m)| = m^n$. Cada elemento es un vector de longitud $n$
donde la posicion $i$ indica el indice del aula asignada al curso $i$.

### Implementacion

```scala
def generarAsignaciones(n: Int, m: Int): Vector[Asignacion] = {
  if (n == 0)
    Vector(Vector())
  else {
    val anteriorAsignacion = generarAsignaciones(n - 1, m)
    anteriorAsignacion.flatMap { asignacion =>
      (0 until m).map { aula => aula +: asignacion }
    }
  }
}
```

### Argumentacion de correccion

Se demuestra por induccion sobre $n$.

Queremos demostrar que:

$$
\forall n, m \in \mathbb{N} : P_f(n, m) == f(n, m) = \{0,\ldots,m-1\}^n
$$

**Caso base:** $n = 0$

$$
\text{Vector}(\text{Vector}()) = \{()\} = \{0,\ldots,m-1\}^0
$$

El producto cartesiano de cero factores contiene exactamente un
elemento: la tupla vacia. La implementacion retorna `Vector(Vector())`,
que corresponde a ese unico elemento. La prueba **"0 cursos genera
una asignacion vacia"** verifica este caso. ✓

**Caso inductivo:** $n > 0$

Hipotesis de induccion:

$$
\text{anteriorAsignacion} = P_f(n-1, m) = \{0,\ldots,m-1\}^{n-1}
$$

La operacion `flatMap` extiende cada asignacion de longitud $n-1$
agregando al frente cada aula posible $a \in \{0,\ldots,m-1\}$:

$$
P_f(n, m) = \bigcup_{a=0}^{m-1} \{ a \;::\; \alpha \mid \alpha \in \{0,\ldots,m-1\}^{n-1} \}
$$

Esta union es exactamente el producto cartesiano $\{0,\ldots,m-1\} \times \{0,\ldots,m-1\}^{n-1} = \{0,\ldots,m-1\}^n$.

La cardinalidad del resultado es:

$$
|P_f(n, m)| = m \cdot |P_f(n-1, m)| \xrightarrow{HI} m \cdot m^{n-1} = m^n
$$

Las pruebas verifican este resultado:

```scala
assert(generarAsignaciones(2,3).length == 9)   // 3^2 = 9
assert(generarAsignaciones(3,2).length == 8)   // 2^3 = 8
assert(generarAsignaciones(4,2).length == 16)  // 2^4 = 16
```

Ademas, la prueba **"todas las asignaciones son diferentes"** verifica
que no hay duplicados (`resultado.distinct.length == resultado.length`),
y la prueba **"ninguna asignacion tiene aulas fuera del rango"**
verifica que todos los valores estan en $\{0,\ldots,m-1\}$. ✓

**Conclusion:**

$$
\forall n, m \in \mathbb{N} : P_f(n, m) == \{0,\ldots,m-1\}^n \quad \checkmark
$$


---

## 2.9 Función  `asignacionOptima`

#### Especificación

Sea $f : \text{Cursos} \times \text{Aulas} \times \text{Distancias} \times \text{Pesos} \to \text{Asignacion} \times \mathbb{N}$ la función que devuelve la asignación de mínimo costo total. Formalmente:

$$
f(C, A, D, w) = \arg\min_{\alpha \in \{0,\ldots,m-1\}^n} \text{CT}^\alpha_{C,A,D}
$$

#### Implementación

```scala
def asignacionOptima(cursos: Cursos, aulas: Aulas, d: Distancias,
                     w: Pesos): (Asignacion, Int) = {
  val todasLasAsignaciones = generarAsignaciones(cursos.length, aulas.length)
  todasLasAsignaciones
    .map(asig => (asig, costoAsignacion(cursos, aulas, d, asig, w)))
    .minBy(_._2)
}
```

#### Argumentación de corrección

Queremos demostrar que:

$$
\forall C, A, D, w : P_f(C, A, D, w) == f(C, A, D, w)
$$

**Demostración:**

1. Por la corrección de `generarAsignaciones` demostrada anteriormente, `todasLasAsignaciones` contiene exactamente todos los elementos de $\{0,\ldots,m-1\}^n$.
2. El `.map` aplica `costoAsignacion` a cada asignación, produciendo el conjunto:
   $$
   S = \{(\alpha, \text{CT}^\alpha_{C,A,D}) \mid \alpha \in \{0,\ldots,m-1\}^n\}
   $$

3. `.minBy(_._2)` selecciona el par $(\alpha^*, c^*)$ tal que:
   $$
   c^* = \min_{(\alpha, c) \in S} c = \min_{\alpha \in \{0,\ldots,m-1\}^n} \text{CT}^\alpha_{C,A,D}
   $$

Como $S$ contiene todas las asignaciones posibles y `.minBy` es exhaustivo sobre $S$, el resultado es efectivamente el mínimo global.

$$
P_f(C, A, D, w) == f(C, A, D, w) \quad \checkmark
$$

**Conclusión:**

$$
\forall C, A, D, w : P_f(C, A, D, w) == \arg\min_{\alpha \in \{0,\ldots,m-1\}^n} \text{CT}^\alpha_{C,A,D} \quad \checkmark
$$

---

## 3.2. Funcion `generarAsignacionesPar` (version paralela)

### Especificacion

Sea $f_\parallel : \mathbb{N} \times \mathbb{N} \to \text{Vector}[\text{Asignacion}]$
la misma funcion matematica que `generarAsignaciones`:

$$
f_\parallel(n, m) = \{0, \ldots, m-1\}^n
$$

La version paralela debe producir el mismo resultado que la secuencial,
con la diferencia de que la iteracion sobre las aulas se realiza en
paralelo mediante la funcion auxiliar `aux`.

### Implementacion

```scala
def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = {
  if (n == 0)
    Vector(Vector())
  else {
    val anteriorAsignacion = generarAsignacionesPar(n - 1, m)
    def aux(vect: Vector[Int]): Vector[Asignacion] = {
      if (vect.length == 1)
        anteriorAsignacion.map(asigAnterior => vect(0) +: asigAnterior)
      else {
        val (vect1, vect2) = vect.splitAt(vect.length / 2)
        val (a, b) = parallel(aux(vect1), aux(vect2))
        a ++ b
      }
    }
    aux((0 until m).toVector)
  }
}
```

### Argumentacion de correccion

Se demuestra en dos partes: primero la correccion de `aux` por
induccion estructural sobre `vect`, y luego la correccion global
de `generarAsignacionesPar` por induccion sobre $n$.

#### Parte 1: Correccion de `aux`

Sea $\text{prev} = \text{anteriorAsignacion}$ el conjunto de asignaciones
de longitud $n-1$ calculado recursivamente. Definimos la especificacion
de `aux` como:

$$
\text{aux}(V) = \bigcup_{a \in V} \{ a \;::\; \alpha \mid \alpha \in \text{prev} \}
$$

Queremos demostrar:

$$
\forall V \subseteq \{0,\ldots,m-1\} : P_\text{aux}(V) == \text{aux}(V)
$$

**Caso base:** $|V| = 1$, es decir $V = \{a\}$

```scala
anteriorAsignacion.map(asigAnterior => vect(0) +: asigAnterior)
```

$$
P_\text{aux}(\{a\}) = \{a \;::\; \alpha \mid \alpha \in \text{prev}\} = \text{aux}(\{a\}) \quad \checkmark
$$

**Caso inductivo:** $|V| > 1$

Sea $V = V_1 \cup V_2$ la particion dada por `splitAt(length/2)`,
con $V_1 \cap V_2 = \emptyset$.

Hipotesis de induccion:

$$
P_\text{aux}(V_1) == \text{aux}(V_1) \qquad P_\text{aux}(V_2) == \text{aux}(V_2)
$$

`parallel` ejecuta `aux(vect1)` y `aux(vect2)` concurrentemente.
Dado que `aux` solo lee `anteriorAsignacion` sin modificarla, no
existe condicion de carrera y los resultados son los mismos que en
ejecucion secuencial:

$$
(a, b) = (P_\text{aux}(V_1),\; P_\text{aux}(V_2)) \xrightarrow{HI} (\text{aux}(V_1),\; \text{aux}(V_2))
$$

La combinacion `a ++ b` produce:

$$
P_\text{aux}(V) = \text{aux}(V_1) \cup \text{aux}(V_2) = \bigcup_{a \in V_1 \cup V_2} \{a \;::\; \alpha \mid \alpha \in \text{prev}\} = \text{aux}(V) \quad \checkmark
$$

La union es disjunta porque $V_1 \cap V_2 = \emptyset$, garantizando
que no hay duplicados en el resultado.

#### Parte 2: Correccion global por induccion sobre $n$

**Caso base:** $n = 0$

Identico a `generarAsignaciones`:

$$
\text{Vector}(\text{Vector}()) = \{0,\ldots,m-1\}^0 \quad \checkmark
$$

**Caso inductivo:** $n > 0$

Hipotesis de induccion:

$$
\text{anteriorAsignacion} = P_{f_\parallel}(n-1, m) = \{0,\ldots,m-1\}^{n-1}
$$

Por la correccion de `aux` demostrada en la Parte 1, con
$V = \{0,\ldots,m-1\}$:

$$
P_{f_\parallel}(n, m) = \text{aux}(\{0,\ldots,m-1\}) = \bigcup_{a=0}^{m-1} \{a \;::\; \alpha \mid \alpha \in \{0,\ldots,m-1\}^{n-1}\}
$$

$$
= \{0,\ldots,m-1\} \times \{0,\ldots,m-1\}^{n-1} = \{0,\ldots,m-1\}^n = f_\parallel(n, m) \quad \checkmark
$$

**Equivalencia con la version secuencial:**

Como $P_f(n, m) = P_{f_\parallel}(n, m) = \{0,\ldots,m-1\}^n$,
ambas funciones producen el mismo conjunto de asignaciones. La prueba
**"contiene la asignacion Vector(0,2,1)"** y **"todas las asignaciones
son diferentes"** verifican que el contenido y la cardinalidad son
correctos:

```scala
assert(generarAsignacionesPar(0, 5) == Vector(Vector()))
assert(generarAsignaciones(3, 3).contains(Vector(0, 2, 1)))
assert(generarAsignaciones(2, 3).distinct.length == resultado.length)
assert(generarAsignaciones(4, 2).length == 16)
```

**Conclusion:**

$$
\forall n, m \in \mathbb{N} : P_{f_\parallel}(n, m) == f_\parallel(n, m) = \{0,\ldots,m-1\}^n \quad \checkmark
$$

$$
\forall n, m \in \mathbb{N} : P_{f_\parallel}(n, m) == P_f(n, m) \quad \square
$$
