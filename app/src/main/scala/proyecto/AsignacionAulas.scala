package proyecto

import scala.util.Random

object AsignacionAulas {

  // Un curso es (id, horaInicio, horaFin, numEstudiantes).
  // Las horas son bloques de 30 min desde las 6:00 a.m. (p. ej. ini=4 → 8:00 a.m.).
  type Curso = (String, Int, Int, Int)
  type Cursos = Vector[Curso]

  // Un aula es (id, capacidad).
  type Aula = (String, Int)
  type Aulas = Vector[Aula]

  // Asignacion(i) = j significa que el curso i se dicta en el aula j; -1 = sin asignar.
  type Asignacion = Vector[Int]

  // Matriz simétrica de distancias entre aulas.
  type Distancias = Vector[Vector[Int]]

  // Pesos: (w_CH, w_CF, w_DE, w_MV).
  type Pesos = (Int, Int, Int, Int)

  // ---------------------------------------------------------------------------
  // Funciones de generación (ya implementadas — NO MODIFICAR)
  // ---------------------------------------------------------------------------

  val random = new Random()

  def cursosAlAzar(n: Int): Cursos =
    Vector.tabulate(n) { i =>
      val ini = random.nextInt(29)
      val dur = random.nextInt(7) + 2
      ("C" + i, ini, ini + dur, random.nextInt(46) + 5)
    }

  def aulasAlAzar(m: Int): Aulas =
    Vector.tabulate(m)(j => ("E" + j, random.nextInt(46) + 15))

  def distanciasAlAzar(m: Int): Distancias = {
    val v = Vector.fill(m, m)(random.nextInt(m * 2) + 1)
    Vector.tabulate(m, m)((i, j) =>
      if (i < j) v(i)(j)
      else if (i == j) 0
      else v(j)(i))
  }

  // ---------------------------------------------------------------------------
  // Funciones de acceso (ya implementadas — NO MODIFICAR)
  // ---------------------------------------------------------------------------

  def idCurso(c: Curso): String = c._1

  def iniCurso(c: Curso): Int = c._2

  def finCurso(c: Curso): Int = c._3

  def estCurso(c: Curso): Int = c._4

  def idAula(a: Aula): String = a._1

  def capAula(a: Aula): Int = a._2

  // ---------------------------------------------------------------------------
  // Funciones a implementar
  // ---------------------------------------------------------------------------

  /** Devuelve true sii los intervalos [ini1, fin1) y [ini2, fin2) se traslapan. */
  def solapan(c1: Curso, c2: Curso): Boolean = {
    val inicio1 = iniCurso(c1) // aqui obtenemos el inicio del curso 1
    val inicio2 = iniCurso(c2) // aqui obtenemos el inicio del curso 2
    val final1 = finCurso(c1) // aqui obtenemos el final del curso 1
    val final2 = finCurso(c2) // aqui obtenemos el final del curso 2
    inicio1 < final2 && inicio2 < final1
    // en este caso si se interponen va a ser porque el curso 1
    // empieza antes de que termine el curso 2 y que el curso 2 empiece
    // antes de que termine el curso 1
  }

  /**
   * Número de pares (i, j) con i < j tales que a(i) == a(j) >= 0
   * y los cursos i y j se solapan.
   */
  def choques(cursos: Cursos, a: Asignacion): Int = {
    val pares = for {
      i <- 0 until cursos.length // se recorre cada curso
      j <- (i + 1) until cursos.length // luego se compara con los que vienen después
      if a(i) >= 0 && a(j) >= 0 // se hace la verificacion para que los cursos tengan una aula asignada
      if a(i) == a(j) // y se verifica si los cursos usan la misma aula
      if solapan(cursos(i), cursos(j)) // ademas que sus horarios se superpongan
    } yield 1 // despues si se cumple todo esto se se cuenta un choque

    // el resultado sera el total de choques que halla en pares
    pares.length
  }

  /** Cantidad de cursos cuya aula asignada tiene capacidad menor al número de estudiantes. */
  def capacidadFallida(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {
    val cursAuls = for {
      i <- (0 until cursos.length)
    } yield (cursos(i)._4, aulas(a(i))._2) //Se crea un vector de tuplas donde esta la cantidad de estudiantes i ,la capacidad del aula j(posicion dada por la asignacion)
    //Condicion capacidadAulas < a cantidad de estudiantes
    cursAuls.count(p => p._2 < p._1) //Count permite contar todas las tuplas que cumplen exactamente la condicion
  }

  /**
   * Suma de (cap(aula_i) - est(curso_i)) para los cursos asignados
   * con capacidad suficiente.
   */
  def desperdicio(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {

    val cursAuls = for {
      i <- (0 until cursos.length)
    } yield (cursos(i)._4, aulas(a(i))._2) //Se crea un vector de tuplas donde esta la cantidad de estudiantes i ,la capacidad del aula j(posicion dada por la asignacion)

    val listaDesperdicio = cursAuls.toList.filter {
      case (cantEst, capacidadAuls) =>
        capacidadAuls >= cantEst //Filtro de las aulas que si tienen suficiente capacidad para almacenar estudiantes
    }.map {
      case (cantEst, capacidadAuls) =>
        capacidadAuls - cantEst //Se resta la capacidad del aula con la cantidad de estudiantes
    } //Se hace esto para poder calcular el desperdicio es decir cuantas aulas quedan libres ejemplo si son 30 - 25 = 5 esos 5 representan los puestos sobrantes
    listaDesperdicio.sum //Al final se suman todos los desperdicios calculados incluyendo si no hubo desperdicio ya que si no lo hubo el valor seria 0(indicando que no hubo nada de desperdicio)
  }

  /**
   * Ordena los cursos asignados por hora de inicio y suma las distancias
   * entre aulas de cursos consecutivos.
   */


  def movilidad(cursos: Cursos, aulas: Aulas, d: Distancias,
                a: Asignacion): Int = {
    val ordenados = cursos.indices.toVector
      .filter(i => a(i) >= 0)
      .sortBy(i => iniCurso(cursos(i)))
    if (ordenados.length < 2) 0
    else
      ordenados.zip(ordenados.tail)
        .map { case (i, j) => d(a(i))(a(j)) }
        .sum
  }

  /** Costo total: w_CH * CH + w_CF * CF + w_DE * DE + w_MV * MV. */
  def costoAsignacion(cursos: Cursos, aulas: Aulas, d: Distancias,
                      a: Asignacion, w: Pesos): Int = {
    val (wCH, wCF, wDE, wMV) = w
    wCH * choques(cursos, a) +
      wCF * capacidadFallida(cursos, aulas, a) +
      wDE * desperdicio(cursos, aulas, a) +
      wMV * movilidad(cursos, aulas, d, a)
  }
  /**
   * Genera todas las asignaciones completas posibles: vectores en {0,..,m-1}^n.
   * El tamaño del resultado es m^n.
   */
  def generarAsignaciones(n: Int, m: Int): Vector[Asignacion] = {
    if (n == 0)
      Vector(Vector()) //Caso base devuelve vector de vector vacio como punto de arranque para construir las posibles asignaciones
    else {
      val anteriorAsignacion = generarAsignaciones(n - 1, m)
      anteriorAsignacion.flatMap {
        asignacion =>
          (0 until m).map { // se crea el punto de partida (0 hasta m-1) ejemplo si m es 2 quedaria (0,1)
            aulas => aulas +: asignacion //Se agrega 0 y 1 ala asignacion anterior la cual va acumulando los resultados de cada marco de pila
          }
      }
    }
  }

  /**
   * Devuelve la asignación de mínimo costo y su costo.
   * Usa generarAsignaciones para explorar el espacio.
   */
  def asignacionOptima(cursos: Cursos, aulas: Aulas, d: Distancias,
                       w: Pesos): (Asignacion, Int) = {
    // generamos todas las combinaciones posibles de aulas para los cursos
    val todasLasAsignaciones = generarAsignaciones(cursos.length, aulas.length)
    // a cada asignacion le calculamos su costo total
    todasLasAsignaciones
      .map(asig => (asig, costoAsignacion(cursos, aulas, d, asig, w)))
      // retornamos la que tenga el menor costo
      .minBy(_._2)
  }
}
