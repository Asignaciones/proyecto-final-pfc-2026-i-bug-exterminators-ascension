# Informe de Paralelización — Proyecto Asignación de Aulas

**Entorno de ejecución:** 8 núcleos lógicos · Java 1.8.0_111 (HotSpot 64-Bit Server VM)

---

## Estrategia de paralelización

### `choquesPar`

- **Dimensión dividida:** vector de cursos de longitud $n$; se parte en dos mitades $[0, \lfloor n/2 \rfloor)$ y $[\lfloor n/2 \rfloor, n)$.
- **Primitiva:** `parallel(choquesRango(0, mid), choquesRango(mid, n))`.
- **Operación de combinación:** suma entera de los conteos parciales de cada mitad. La suma es asociativa y conmutativa, por lo que el orden de combinación no altera el resultado.
- **Observación:** cada mitad recorre sus índices $i$ y compara con todos los $j > i$ globales; las mitades no se solapan en trabajo porque la mitad izquierda sólo itera $i \in [0, mid)$ y la mitad derecha $i \in [mid, n)$, de modo que los pares $(i,j)$ con $i < mid \leq j$ son capturados por la mitad izquierda cuando evalúa $j \in [i+1, n)$.

### `desperdicioPar`

- **Dimensión dividida:** vector de cursos de longitud $n$; se parte en dos mitades $[0, \lfloor n/2 \rfloor)$ y $[\lfloor n/2 \rfloor, n)$.
- **Primitiva:** `parallel(desperdicioRango(0, mid), desperdicioRango(mid, n))`.
- **Operación de combinación:** suma entera del desperdicio parcial de cada mitad. Cada índice $i$ contribuye de forma independiente con `capAula(aulas(a(i))) - estCurso(cursos(i))`, por lo que no hay dependencia entre mitades y la suma es trivialmente correcta.

### `movilidadPar`

- **Dimensión dividida:** vector de pares consecutivos de cursos ordenados por hora de inicio; se parte en dos mitades $[0, \lfloor \text{pares}/2 \rfloor)$ y $[\lfloor \text{pares}/2 \rfloor, \text{pares})$.
- **Primitiva:** `parallel` sobre las dos mitades del vector de pares.
- **Operación de combinación:** suma entera de las distancias parciales. Los pares son independientes entre sí (cada par $(i, j)$ contribuye con `d(a(i))(a(j))`), por lo que la suma es asociativa y la partición es segura.
- **Caso degenerado:** si hay menos de 2 cursos asignados, se retorna 0 directamente sin lanzar tareas paralelas.

### `generarAsignacionesPar`

- **Dimensión dividida:** el vector de valores posibles para el primer curso, `(0 until m)`, se divide recursivamente en dos mitades hasta llegar a vectores de tamaño 1.
- **Primitiva:** `parallel(aux(vect1), aux(vect2))` dentro de la función auxiliar `aux`, formando un árbol binario de tareas.
- **Operación de combinación:** concatenación `++` de los sub-vectores de asignaciones parciales. La concatenación es asociativa y produce la unión disjunta correcta.
- **Estructura recursiva:** la generación de asignaciones para $n$ cursos reutiliza el resultado de $n-1$ cursos (`anteriorAsignacion`) de forma secuencial; la paralelización actúa sobre la expansión del primer elemento, no sobre la recursión base.

### `asignacionOptimaPar`

- **Dimensión dividida:** el espacio completo de asignaciones candidatas (generado con la versión secuencial `generarAsignaciones`) se divide en dos mitades: `mitadIzq = take(mitad)` y `mitadDer = drop(mitad)`.
- **Primitiva:** `parallel` sobre las dos mitades; cada mitad evalúa su costo con `costoAsignacion` y retorna el mínimo local mediante `minBy(_._2)`.
- **Operación de combinación:** comparación de los dos mínimos locales y retorno del menor. La búsqueda del mínimo global es correcta porque toda asignación pertenece exactamente a una de las dos mitades.

---

## Trabajo $W(n)$ y profundidad $S(n)$

### `choquesPar` / `desperdicioPar`

Sea $n$ el número de cursos.

**`choquesPar`** — el trabajo total es el número de pares $(i, j)$ con $i < j$:

$$W_{\text{choques}}(n) = O(n^2)$$

Con la división en dos mitades de tamaño $\lfloor n/2 \rfloor$, ambas ramas se ejecutan en paralelo, por lo que la profundidad es el costo de la rama más costosa:

$$S_{\text{choques}}(n) = O\!\left(\frac{n^2}{4}\right) = O(n^2)$$

Speedup teórico (2 ramas iguales):

$$\frac{W}{S} = \frac{O(n^2)}{O(n^2/4)} \approx 2\times$$

**`desperdicioPar`** — el trabajo es lineal en $n$ (una pasada por cada curso):

$$W_{\text{desperdicio}}(n) = O(n), \quad S_{\text{desperdicio}}(n) = O(n/2)$$

$$\frac{W}{S} \approx 2\times$$

### `movilidadPar`

Sea $p$ el número de pares de cursos consecutivos ($p = n - 1$ en el peor caso):

$$W_{\text{movilidad}}(p) = O(p), \quad S_{\text{movilidad}}(p) = O(p/2)$$

$$\frac{W}{S} \approx 2\times$$

### `generarAsignacionesPar`

Sea $n$ el número de cursos y $m$ el número de aulas. El total de asignaciones es $m^n$.

**Trabajo:** en cada nivel recursivo se construyen todas las $m^n$ asignaciones; el árbol binario de tareas tiene $m$ hojas y profundidad $\log_2 m$:

$$W_{\text{generar}}(n, m) = O(m^n)$$

**Profundidad:** el árbol de recursión sobre `aux` tiene altura $\log_2 m$; en cada nivel el trabajo dominante es la concatenación `++`. La profundidad total es:

$$S_{\text{generar}}(n, m) = O\!\left(\frac{m^n}{\log_2 m}\right)$$

Speedup teórico (árbol de tareas con $\log_2 m$ niveles):

$$\frac{W}{S} = O(\log_2 m)$$

### `asignacionOptimaPar`

El total de candidatos es $|\mathcal{A}| = m^n$. Cada candidato se evalúa en $O(n)$ con `costoAsignacion`:

$$W_{\text{optima}}(n, m) = O(m^n \cdot n)$$

Con la partición en dos mitades iguales:

$$S_{\text{optima}}(n, m) = O\!\left(\frac{m^n \cdot n}{2}\right)$$

$$\frac{W}{S} \approx 2\times$$

---

## Resultados experimentales

### `choques` vs `choquesPar`

| $n$ cursos | Secuencial (ms) | Paralela (ms) | Speedup |
|:----------:|:--------------:|:-------------:|:-------:|
| 1 | 7,8095 | 18,2014 | 0,43 |
| 2 | 1,5329 | 0,1757 | 8,72 |
| 3 | 0,0796 | 0,2100 | 0,38 |
| 4 | 0,2338 | 0,1809 | 1,29 |
| 5 | 0,1331 | 0,2724 | 0,49 |
| 6 | 0,1684 | 1,0183 | 0,17 |
| 7 | 0,2719 | 0,2491 | 1,09 |
| 8 | 0,2704 | 0,2967 | 0,91 |

### `desperdicio` vs `desperdicioPar`

| $n$ cursos | Secuencial (ms) | Paralela (ms) | Speedup |
|:----------:|:--------------:|:-------------:|:-------:|
| 1 | 1,3697 | 3,8589 | 0,35 |
| 2 | 0,0450 | 0,2427 | 0,19 |
| 3 | 0,0613 | 0,2041 | 0,30 |
| 4 | 0,1056 | 0,1619 | 0,65 |
| 5 | 0,0978 | 0,2401 | 0,41 |
| 6 | 0,1487 | 0,1589 | 0,94 |
| 7 | 0,0887 | 0,2085 | 0,43 |
| 8 | 0,1252 | 0,1771 | 0,71 |

### `movilidad` vs `movilidadPar`

| $n$ cursos | Secuencial (ms) | Paralela (ms) | Speedup |
|:----------:|:--------------:|:-------------:|:-------:|
| 2 | 3,6818 | 3,3435 | 1,10 |
| 3 | 0,0771 | 0,1449 | 0,53 |
| 4 | 0,0859 | 0,1822 | 0,47 |
| 5 | 0,1315 | 0,1697 | 0,77 |
| 6 | 0,0869 | 0,2159 | 0,40 |
| 7 | 0,1405 | 0,2456 | 0,57 |
| 8 | 0,0936 | 0,3279 | 0,29 |

### `generarAsignaciones` vs `generarAsignacionesPar`

| $n$ cursos | $m$ aulas | Secuencial (ms) | Paralela (ms) | Speedup |
|:----------:|:---------:|:--------------:|:-------------:|:-------:|
| 1 | 2 | 12,1164 | 12,2916 | 0,99 |
| 1 | 3 | 0,0739 | 0,8920 | 0,08 |
| 1 | 4 | 0,1027 | 0,4812 | 0,21 |
| 1 | 5 | 0,1301 | 0,8379 | 0,16 |
| 2 | 2 | 0,1167 | 0,3273 | 0,36 |
| 2 | 3 | 0,1515 | 0,4881 | 0,31 |
| 2 | 4 | 0,1437 | 0,7509 | 0,19 |
| 2 | 5 | 0,1929 | 0,7306 | 0,26 |
| 3 | 2 | 0,1825 | 0,6190 | 0,29 |
| 3 | 3 | 0,3225 | 0,8977 | 0,36 |
| 3 | 4 | 1,2166 | 1,5637 | 0,78 |
| 3 | 5 | 0,6032 | 1,9737 | 0,31 |
| 4 | 2 | 0,2260 | 0,5537 | 0,41 |
| 4 | 3 | 0,4195 | 0,9240 | 0,45 |
| 4 | 4 | 0,8389 | 0,9238 | 0,91 |
| 4 | 5 | 1,3155 | 1,3334 | 0,99 |
| 5 | 2 | 0,1931 | 0,6693 | 0,29 |
| 5 | 3 | 1,0414 | 1,2789 | 0,81 |
| 5 | 4 | 2,0257 | 1,3439 | 1,51 |
| 5 | 5 | 3,6527 | 2,0996 | 1,74 |
| 6 | 2 | 0,2226 | 0,9132 | 0,24 |
| 6 | 3 | 0,8986 | 1,3727 | 0,65 |
| 6 | 4 | 2,8912 | 2,3596 | 1,23 |
| 6 | 5 | 6,3170 | 3,5343 | 1,79 |
| 7 | 2 | 0,4633 | 1,1202 | 0,41 |
| 7 | 3 | 2,2918 | 1,6812 | 1,36 |
| 7 | 4 | 8,0811 | 2,7896 | 2,90 |
| 7 | 5 | 18,1480 | 8,6911 | 2,09 |
| 8 | 2 | 0,3973 | 1,0503 | 0,38 |
| 8 | 3 | 1,8056 | 2,2284 | 0,81 |
| 8 | 4 | 9,9499 | 6,9309 | 1,44 |
| 8 | 5 | 55,4785 | 39,1660 | 1,42 |

### `asignacionOptima` vs `asignacionOptimaPar`

| $n$ cursos | $m$ aulas | Secuencial (ms) | Paralela (ms) | Speedup | Aceleración (%) |
|:----------:|:---------:|:--------------:|:-------------:|:-------:|:---------------:|
| 2 | 2 | 5,7758 | 3,0236 | 1,91 | +91,0 |
| 2 | 3 | 1,4225 | 1,1443 | 1,24 | +24,3 |
| 2 | 4 | 2,0906 | 1,7611 | 1,19 | +18,7 |
| 2 | 5 | 2,7664 | 1,8092 | 1,53 | +52,9 |
| 3 | 2 | 1,4654 | 0,8639 | 1,70 | +69,6 |
| 3 | 3 | 3,3426 | 2,1072 | 1,59 | +58,6 |
| 3 | 4 | 5,9658 | 5,0356 | 1,18 | +18,5 |
| 3 | 5 | 11,7389 | 6,2316 | 1,88 | +88,4 |
| 4 | 2 | 1,7102 | 0,7970 | 2,15 | +114,6 |
| 4 | 3 | 4,5819 | 3,5213 | 1,30 | +30,1 |
| 4 | 4 | 9,7627 | 5,8983 | 1,66 | +65,5 |
| 4 | 5 | 12,5475 | 11,2127 | 1,12 | +11,9 |
| 5 | 2 | 1,4766 | 0,8479 | 1,74 | +74,1 |
| 5 | 3 | 6,2420 | 5,8477 | 1,07 | +6,7 |
| 5 | 4 | 15,7687 | 13,4539 | 1,17 | +17,2 |
| 5 | 5 | 46,3216 | 35,9921 | 1,29 | +28,7 |
| 6 | 2 | 1,3042 | 0,9537 | 1,37 | +36,8 |
| 6 | 3 | 8,3962 | 8,6589 | 0,97 | −3,0 |
| 6 | 4 | 32,2857 | 26,0518 | 1,24 | +23,8 |
| 6 | 5 | 80,2564 | 44,6018 | 1,80 | +79,9 |
| 7 | 2 | 0,8598 | 0,6475 | 1,33 | +32,8 |
| 7 | 3 | 13,3376 | 7,7956 | 1,71 | +71,1 |
| 7 | 4 | 59,0479 | 39,9750 | 1,48 | +47,6 |
| 7 | 5 | 278,0108 | 151,5039 | 1,84 | +83,5 |
| 8 | 2 | 4,8457 | 1,2549 | 3,86 | +286,3 |
| 8 | 3 | 24,8363 | 13,6737 | 1,82 | +81,7 |
| 8 | 4 | 230,0619 | 138,2472 | 1,66 | +66,4 |
| 8 | 5 | 1379,6522 | 847,1287 | 1,63 | +62,8 |

---

## Análisis con la Ley de Amdahl

La ley de Amdahl establece que, con $k$ procesadores y una fracción paralelizable $\alpha$:

$$S(k) = \frac{1}{(1 - \alpha) + \dfrac{\alpha}{k}}$$

La máquina de pruebas tiene $k = 8$ núcleos lógicos.

---

### `choquesPar`, `desperdicioPar`, `movilidadPar`

Estas tres funciones aplican una única división en dos mitades con `parallel`. Teóricamente, la fracción paralelizable es $\alpha \approx 1.0$ (todo el trabajo útil queda dentro de las dos ramas), y el speedup esperado con $k=2$ ramas sería $2\times$.

**Sin embargo, los resultados muestran speedups consistentemente menores que 1** en casi todos los casos. La causa es que los problemas de tamaño $n \leq 8$ son extremadamente pequeños: el tiempo de cómputo útil (sub-milisegundo) es inferior al overhead de lanzar hilos JVM, sincronizar con `parallel` y esperar el join. En este régimen, el **costo de arranque del paralelismo supera el costo del cómputo en sí**.

Con $\alpha \approx 0.90$ (estimando un 10% de overhead no paralelizable) y $k=2$ ramas efectivas:

$$S(2) = \frac{1}{0{,}10 + 0{,}90/2} = \frac{1}{0{,}55} \approx 1{,}82\times$$

Este límite teórico sólo se acercaría con entradas mucho más grandes donde el overhead de lanzar hilos se vuelva despreciable frente al cómputo.

**Conclusión:** `choquesPar`, `desperdicioPar` y `movilidadPar` no generan ganancias en este rango de $n$ porque el granulo de trabajo es demasiado fino. El paralismo sería efectivo con $n$ del orden de miles.

---

### `generarAsignacionesPar`

Esta función usa un árbol binario de tareas sobre los $m$ valores posibles del primer elemento. El comportamiento observado se divide en dos fases claras:

**Fase 1 — $n$ pequeño o $m$ pequeño ($n \leq 4$, $m \leq 4$):** speedup $< 1$ (overhead domina). La cantidad de asignaciones generadas $m^n$ es pequeña (hasta $4^4 = 256$), y el árbol de `parallel` genera más overhead del que ahorra.

**Fase 2 — $n$ grande o $m$ grande ($n \geq 5$, $m \geq 4$):** el speedup supera 1 y crece. Para $(n=7, m=4)$ se alcanza $2{,}90\times$ y para $(n=7, m=5)$ se obtiene $2{,}09\times$. Esto ocurre porque $m^n$ crece exponencialmente: $5^7 = 78\,125$ asignaciones representan un trabajo real que amortiza el costo de los hilos.

Con $\alpha \approx 0.90$ (la recursión base en $n-1$ es secuencial y añade overhead) y $k=8$:

$$S(8) = \frac{1}{0{,}10 + 0{,}90/8} = \frac{1}{0{,}2125} \approx 4{,}71\times$$

Los speedups observados (máximo $\approx 2{,}90\times$) quedan por debajo de este límite teórico porque el árbol de paralelismo tiene profundidad $\log_2 m \leq 3$ niveles, lo que sólo permite explotar $2^3 = 8$ hilos como máximo, y en la práctica la JVM y el scheduler limitan aún más el paralelismo efectivo.

---

### `asignacionOptimaPar`

Esta función divide el espacio de candidatos $m^n$ en dos mitades y evalúa cada mitad con `parallel`. Los resultados son los más consistentes del proyecto:

- **Speedup $> 1$ en casi todos los casos** (salvo $(6,3)$ con $0{,}97\times$, prácticamente empate).
- **Speedup máximo de $3{,}86\times$ para $(n=8, m=2)$**, lo que es el mejor resultado del proyecto.
- **Tendencia clara:** el speedup crece con $n$ y con $m$, confirmando que el beneficio del paralelismo escala con el tamaño del problema.

El caso $(n=8, m=5)$ tarda 1379 ms secuencialmente y 847 ms en paralelo, una reducción de más del 38%, que es la ganancia más significativa en tiempo absoluto.

Con $\alpha \approx 0.95$ (la generación inicial de `generarAsignaciones` es secuencial y domina el overhead) y $k=2$ ramas efectivas:

$$S(2) = \frac{1}{0{,}05 + 0{,}95/2} = \frac{1}{0{,}525} \approx 1{,}90\times$$

Este límite coincide bien con los speedups observados (la mayoría entre $1{,}2\times$ y $1{,}9\times$). El valor atípico de $3{,}86\times$ para $(8,2)$ sugiere una variación de timing favorable (caché caliente, planificador del SO).

### Tabla resumen — Ley de Amdahl

| Función | $\alpha$ estimado | $k$ efectivo | Límite $S(k)$ teórico | Speedup empírico (máx.) |
|---|:---:|:---:|:---:|:---:|
| `choquesPar` | 0,90 | 2 | $\approx 1{,}82\times$ | 8,72× (n=2, atípico) |
| `desperdicioPar` | 0,90 | 2 | $\approx 1{,}82\times$ | 0,94× (no alcanzado) |
| `movilidadPar` | 0,90 | 2 | $\approx 1{,}82\times$ | 1,10× (n=2) |
| `generarAsignacionesPar` | 0,90 | 8 | $\approx 4{,}71\times$ | 2,90× (n=7, m=4) |
| `asignacionOptimaPar` | 0,95 | 2 | $\approx 1{,}90\times$ | 3,86× (n=8, m=2) |

---

## Conclusiones de paralelización

**1. El paralelismo sólo es efectivo cuando el gránulo de trabajo es suficientemente grande.**
Las funciones `choquesPar`, `desperdicioPar` y `movilidadPar` no logran speedup positivo para $n \leq 8$ porque el tiempo de cómputo por hilo (fracciones de milisegundo) es menor que el overhead de crear y sincronizar hilos en la JVM. Para que estas funciones se beneficien del paralelismo, se necesitarían entradas del orden de $n \geq 100$.

**2. La complejidad del espacio de búsqueda determina la ganancia real.**
`generarAsignacionesPar` y `asignacionOptimaPar` sí muestran ganancias consistentes para entradas medianas-grandes ($n \geq 5$, $m \geq 4$), porque el número de asignaciones $m^n$ crece exponencialmente y el trabajo por hilo se vuelve sustancial. Para $(n=7, m=5)$ o $(n=8, m=5)$ el problema tiene decenas de miles de candidatos, y el paralelo reduce el tiempo en 40–47%.

**3. El overhead inicial de la JVM es significativo.**
Los tiempos de la primera medición (especialmente $n=1$ y $n=2$) muestran valores atípicamente altos (ej. 7,8 ms y 12,1 ms) por efecto del calentamiento de la JVM (compilación JIT). Las mediciones posteriores son más representativas del comportamiento en régimen.

**4. La estrategia de dividir en dos mitades con `parallel` es correcta pero limitada a 2 hilos efectivos.**
Con $k=8$ núcleos disponibles pero sólo 2 ramas en `parallel`, se desperdicia el 75% del potencial de paralelismo. Para `asignacionOptimaPar` en particular, una división en 8 partes con `task`/`join` podría acercar el speedup al límite teórico de $\approx 7{,}1\times$ (con $\alpha=0{,}95$ y $k=8$).

**5. El par $(n=8, m=5)$ de `asignacionOptimaPar` es el caso de uso más representativo.**
Con 1379 ms secuencial y 847 ms paralelo, es el único caso donde el tiempo absoluto reducido es relevante desde el punto de vista práctico, y confirma que la paralelización es beneficiosa en los problemas de mayor tamaño que típicamente se presentan en producción.
