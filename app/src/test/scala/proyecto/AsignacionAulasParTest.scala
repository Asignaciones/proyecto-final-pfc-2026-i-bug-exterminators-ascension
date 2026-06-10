package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._
import AsignacionAulasPar._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)

  test("choquesPar: asignacion [0,0,1] tiene 1 choque") {
    assert(choquesPar(c1, Vector(0, 0, 1)) == 1)
  }

  test("choquesPar: asignacion [0,1,0] no tiene choques") {
    assert(choquesPar(c1, Vector(0, 1, 0)) == 0)
  }

  test("desperdicioPar: asignacion [0,0,1] tiene desperdicio 25") {
    assert(desperdicioPar(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("movilidadPar: asignacion [0,0,1] tiene movilidad 3") {
    assert(movilidadPar(c1, a1, d1, Vector(0, 0, 1)) == 3)
  }

  test("generarAsignacionesPar: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignacionesPar(2, 2).length == 4)
  }

  test("asignacionOptimaPar: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costo <= 37)
  }

  test("asignacionOptimaPar: el costo optimo es menor al de [0,0,1] (1031)") {
    val (_, costo) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costo < 1031)
  }

  test("asignacionOptimaPar: el resultado coincide con la version secuencial") {
    val (_, costoSec) = asignacionOptima(c1, a1, d1, w)
    val (_, costoPar) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costoPar == costoSec)
  }

  test("asignacionOptimaPar: ejemplo 2, el costo optimo no supera el de [0,1,0,1] (155)") {
    val c2 = Vector(("F01", 0, 4, 40), ("F02", 4, 8, 25), ("F03", 8, 12, 50), ("F04", 12, 16, 15))
    val a2 = Vector(("S201", 45), ("S202", 30))
    val d2 = Vector(Vector(0, 5), Vector(5, 0))
    val (_, costo) = asignacionOptimaPar(c2, a2, d2, (1000, 100, 1, 2))
    assert(costo <= 155)
  }

  test("asignacionOptimaPar: la optima par es menor o igual a cualquier otra asignacion") {
    val (_, costoOptimo) = asignacionOptimaPar(c1, a1, d1, w)
    val todasLasAsignaciones = generarAsignaciones(c1.length, a1.length)
    val todosLosCostos = todasLasAsignaciones.map(a => costoAsignacion(c1, a1, d1, a, w))
    assert(todosLosCostos.forall(c => costoOptimo <= c))
  }

  test("asignacionOptimaPar: con un solo curso retorna el aula de menor desperdicio") {
    val cursos = Vector(("C1", 0, 4, 20))
    val aulas  = Vector(("E1", 30), ("E2", 50))
    val dist   = Vector(Vector(0, 3), Vector(3, 0))
    val (_, costo) = asignacionOptimaPar(cursos, aulas, dist, (1000, 100, 1, 2))
    assert(costo == 10)
  }
}
