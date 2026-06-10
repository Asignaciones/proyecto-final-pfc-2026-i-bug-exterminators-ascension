# Informe de Corrección
 
---

## 1. Función `solapan`

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

## 2. Función `choques`

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

## 3. Función  `asignacionOptima`

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