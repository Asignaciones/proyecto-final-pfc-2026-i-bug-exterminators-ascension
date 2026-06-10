package proyecto

import common._
import AsignacionAulas._

object AsignacionAulasPar {

  /** Versión paralela de choques: divide el vector de cursos en dos mitades. */
  def choquesPar(cursos: Cursos, a: Asignacion): Int = ???

  /** Versión paralela de desperdicio: divide el vector de cursos en dos mitades. */
  def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = ???

  /** Versión paralela de movilidad: divide el vector de cursos en dos mitades. */
  def movilidadPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                   a: Asignacion): Int = ???

  /**
   * Versión paralela de generarAsignaciones:
   * paraleliza la construcción usando parallel sobre los valores del primer curso.
   */
  def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = ???

  /**
   * Versión paralela de asignacionOptima:
   * divide el espacio de candidatos en dos mitades y combina los mínimos.
   */
  def asignacionOptimaPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                          w: Pesos): (Asignacion, Int) = {
    // generamos todas las asignaciones posibles
    val todasLasAsignaciones = generarAsignaciones(cursos.length, aulas.length)
    // dividimos el espacio en dos mitades
    val mitad = todasLasAsignaciones.length / 2
    val mitadIzq = todasLasAsignaciones.take(mitad)
    val mitadDer = todasLasAsignaciones.drop(mitad)
    // evaluamos cada mitad en paralelo
    val (minimoIzq, minimoDer) = parallel(
      mitadIzq.map(asig => (asig, costoAsignacion(cursos, aulas, d, asig, w))).minBy(_._2),
      mitadDer.map(asig => (asig, costoAsignacion(cursos, aulas, d, asig, w))).minBy(_._2)
    )
    // retornamos el minimo entre los dos resultados
    if (minimoIzq._2 <= minimoDer._2) minimoIzq else minimoDer
  }
}
