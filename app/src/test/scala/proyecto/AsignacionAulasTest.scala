package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasTest extends AnyFunSuite {

  // Ejemplo 1 del enunciado
  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40)) //30
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)
  val a: Asignacion = Vector(0,1,0)
  // solapan
  test("solapan: M01[4,8) y M02[6,10) se solapan") {
    assert(solapan(("M01", 4, 8, 25), ("M02", 6, 10, 30)))
  }

  test("solapan: M01[4,8) y M03[12,16) no se solapan") {
    assert(!solapan(("M01", 4, 8, 25), ("M03", 12, 16, 20)))
  }

  test("solapan: cursos adyacentes [0,4) y [4,8) no se solapan") {
    assert(!solapan(("A", 0, 4, 10), ("B", 4, 8, 10)))
  }

  test("solapan: mismo horario exacto [4,8) y [4,8) se solapan") {
    assert(solapan(("X", 4, 8, 20), ("Y", 4, 8, 20)))
  }

  test("solapan: un curso dentro de otro [2,10) y [4,6) se solapan") {
    assert(solapan(("X", 2, 10, 20), ("Y", 4, 6, 15)))
  }

  test("solapan: se tocan al final [0,6) y [6,10) no se solapan") {
    assert(!solapan(("X", 0, 6, 20), ("Y", 6, 10, 15)))
  }

  test("solapan: separados con brecha [0,3) y [5,8) no se solapan") {
    assert(!solapan(("X", 0, 3, 20), ("Y", 5, 8, 15)))
  }

  test("solapan: cruce parcial invertido [6,10) y [4,8) se solapan") {
    assert(solapan(("X", 6, 10, 20), ("Y", 4, 8, 15)))
  }

  // choques
  test("choques: asignacion [0,0,1] tiene 1 choque (M01 y M02 en E101)") {
    assert(choques(c1, Vector(0, 0, 1)) == 1)
  }

  test("choques: asignacion [0,1,0] no tiene choques") {
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  test("choques: todos en aulas distintas, no hay choques") {
    val cursos = Vector(("C1", 0, 4, 20), ("C2", 0, 4, 20), ("C3", 0, 4, 20))
    val asig   = Vector(0, 1, 2)
    assert(choques(cursos, asig) == 0)
  }

  test("choques: tres cursos en la misma aula y todos se solapan, hay 3 choques") {
    val cursos = Vector(("C1", 0, 6, 20), ("C2", 2, 8, 20), ("C3", 4, 10, 20))
    val asig   = Vector(0, 0, 0)
    assert(choques(cursos, asig) == 3)
  }

  test("choques: cursos sin asignar (-1) no cuentan como choque") {
    val cursos = Vector(("C1", 0, 6, 20), ("C2", 2, 8, 20))
    val asig   = Vector(-1, -1)
    assert(choques(cursos, asig) == 0)
  }

  test("choques: dos pares de choques en aulas distintas") {
    // C1 y C2 chocan en aula 0, C3 y C4 chocan en aula 1
    val cursos = Vector(("C1", 0, 6, 20), ("C2", 3, 9, 20), ("C3", 0, 6, 20), ("C4", 3, 9, 20))
    val asig   = Vector(0, 0, 1, 1)
    assert(choques(cursos, asig) == 2)
  }

  test("choques: misma aula pero no se solapan, no hay choque") {
    val cursos = Vector(("C1", 0, 4, 20), ("C2", 4, 8, 20), ("C3", 8, 12, 20))
    val asig   = Vector(0, 0, 0)
    assert(choques(cursos, asig) == 0)
  }

  // capacidadFallida
  test("capacidadFallida: asignacion [0,0,1] no falla capacidad") {
    assert(capacidadFallida(c1, a1, Vector(0, 0, 1)) == 0)
  }
  //
  test("capacidadFallida: cursos asignados a aulas con capacidad suficiente") {
    val cursos = Vector(
      ("A",0,2,50),
      ("B",2,4,20)
    )

    val aulas = Vector(
      ("E1",30),
      ("E2",60)
    )

    val a1 = Vector(1,0)
    val n = capacidadFallida(cursos,aulas,a1)
    assert(n == 0)
  }

  test("capacidadFallida: un curso falla") {
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )

    val cursos = Vector(
      ("C1",0,2,25),
      ("C2",2,4,35),
      ("C3",4,6,45),
      ("C4",6,8,55)
    )

    assert(
      capacidadFallida(
        cursos,
        aulas,
        Vector(0,1,2,2)
      ) == 1
    )
  }

  test("capacidadFallida: dos cursos fallan") {
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )

    val cursos = Vector(
      ("C1",0,2,25),
      ("C2",2,4,35),
      ("C3",4,6,45),
      ("C4",6,8,55)
    )

    assert(
      capacidadFallida(
        cursos,
        aulas,
        Vector(0,0,2,2)
      ) == 2
    )
  }

  test("capacidadFallida: todos los cursos fallan") {
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )

    val cursos = Vector(
      ("C1",0,2,60),
      ("C2",2,4,70),
      ("C3",4,6,80)
    )

    assert(
      capacidadFallida(
        cursos,
        aulas,
        Vector(0,1,2)
      ) == 3
    )
  }

  test("capacidadFallida: varios cursos comparten la misma aula") {
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )

    val cursos = Vector(
      ("C1",0,2,25),
      ("C2",2,4,35),
      ("C3",4,6,45),
      ("C4",6,8,55)
    )

    assert(
      capacidadFallida(
        cursos,
        aulas,
        Vector(0,0,0,0)
      ) == 3
    )
  }

  // desperdicio
  test("desperdicio: asignacion [0,0,1] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E101(30)-M02(30)=0, E102(40)-M03(20)=20 → 25
    assert(desperdicio(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("desperdicio: asignacion [0,1,0] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E102(40)-M02(30)=10, E101(30)-M03(20)=10 → 25
    assert(desperdicio(c1, a1, Vector(0, 1, 0)) == 25)
  }

  //
  test("desperdicio: capacidad exacta produce desperdicio 0") {
    val cursos = Vector(
      ("A",0,2,30),
      ("B",2,4,40),
      ("C",4,6,50)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )
    assert(desperdicio(cursos,aulas,Vector(0,1,2)) == 0)
  }

  test("desperdicio: todos los cursos caben y generan desperdicio") {
    val cursos = Vector(
      ("A",0,2,10),
      ("B",2,4,20),
      ("C",4,6,30)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )
    println(desperdicio(cursos,aulas,Vector(0,1,2)))
    assert(desperdicio(cursos,aulas,Vector(0,1,2)) == 60)
  }

  test("desperdicio: cursos sin capacidad suficiente no aportan desperdicio") {
    val cursos = Vector(
      ("A",0,2,35),
      ("B",2,4,20)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",40)
    )
    assert(desperdicio(cursos,aulas,Vector(0,1)) == 20)
  }

  test("desperdicio: varios cursos comparten la misma aula") {
    val cursos = Vector(
      ("A",0,2,20),
      ("B",2,4,25),
      ("C",4,6,30)
    )
    val aulas = Vector(
      ("E1",40)
    )
    assert(desperdicio(cursos,aulas,Vector(0,0,0)) == 45)
  }

  test("desperdicio: la asignacion determina el aula usada") {
    val cursos = Vector(
      ("A",0,2,50),
      ("B",2,4,20)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",60)
    )
    assert(desperdicio(cursos,aulas,Vector(1,0)) == 20)
  }

  // movilidad
  test("movilidad: asignacion [0,1,0] con cursos del ejemplo 1, distancia 6") {
    assert(movilidad(c1, a1, d1, Vector(0, 1, 0)) == 6)
  }

  test("movilidad: asignacion [0,0,1] con cursos del ejemplo 1, distancia 3") {
    assert(movilidad(c1, a1, d1, Vector(0, 0, 1)) == 3)
  }

  test("movilidad: un solo curso asignado, movilidad 0") {
    val cursos = Vector(("C1", 0, 4, 10))
    val aulas  = Vector(("E1", 20), ("E2", 20))
    val dist   = Vector(Vector(0, 5), Vector(5, 0))
    assert(movilidad(cursos, aulas, dist, Vector(0)) == 0)
  }

  test("movilidad: todos en la misma aula, distancia 0") {
    val cursos = Vector(("C1", 0, 4, 10), ("C2", 5, 9, 10), ("C3", 10, 14, 10))
    val aulas  = Vector(("E1", 20), ("E2", 20))
    val dist   = Vector(Vector(0, 4), Vector(4, 0))
    assert(movilidad(cursos, aulas, dist, Vector(0, 0, 0)) == 0)
  }

  test("movilidad: cursos desordenados se ordenan por inicio antes de sumar") {
    // C2 empieza antes que C1, entonces el orden es C2 -> C1 -> C3
    // dist(1->0) + dist(0->1) = 4 + 4 = 8
    val cursos = Vector(("C1", 6, 10, 10), ("C2", 0, 4, 10), ("C3", 12, 16, 10))
    val aulas  = Vector(("E1", 20), ("E2", 20))
    val dist   = Vector(Vector(0, 4), Vector(4, 0))
    assert(movilidad(cursos, aulas, dist, Vector(0, 1, 1)) == 8)
  }
  // costoAsignacion
  test("costoAsignacion: asignacion [0,0,1] cuesta 1031") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 0, 1), w) == 1031)
  }

  test("costoAsignacion: asignacion [0,1,0] cuesta 37") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 1, 0), w) == 37)
  }

  test("costoAsignacion: sin choques ni fallos ni movilidad, solo desperdicio") {
    val cursos = Vector(("C1", 0, 4, 10))
    val aulas  = Vector(("E1", 30))
    val dist   = Vector(Vector(0))
    val pesos  = (1000, 100, 1, 2)
    // DE = 30-10 = 20, todo lo demas 0
    assert(costoAsignacion(cursos, aulas, dist, Vector(0), pesos) == 20)
  }

  test("costoAsignacion: ejemplo 2 asignacion [0,1,0,1] cuesta 155") {
    val c2 = Vector(("F01", 0, 4, 40), ("F02", 4, 8, 25), ("F03", 8, 12, 50), ("F04", 12, 16, 15))
    val a2 = Vector(("S201", 45), ("S202", 30))
    val d2 = Vector(Vector(0, 5), Vector(5, 0))
    assert(costoAsignacion(c2, a2, d2, Vector(0, 1, 0, 1), (1000, 100, 1, 2)) == 155)
  }

  test("costoAsignacion: ejemplo 2 asignacion [0,1,1,0] cuesta 160") {
    val c2 = Vector(("F01", 0, 4, 40), ("F02", 4, 8, 25), ("F03", 8, 12, 50), ("F04", 12, 16, 15))
    val a2 = Vector(("S201", 45), ("S202", 30))
    val d2 = Vector(Vector(0, 5), Vector(5, 0))
    assert(costoAsignacion(c2, a2, d2, Vector(0, 1, 1, 0), (1000, 100, 1, 2)) == 160)
  }

  test("costoAsignacion: todos en la misma aula con choque, penalizacion alta") {
    val cursos = Vector(("C1", 0, 6, 10), ("C2", 3, 9, 10))
    val aulas  = Vector(("E1", 20))
    val dist   = Vector(Vector(0))
    val pesos  = (1000, 100, 1, 2)
    // CH=1, CF=0, DE=10+10=20, MV=0
    assert(costoAsignacion(cursos, aulas, dist, Vector(0, 0), pesos) == 1020)
  }

  test("costoAsignacion: curso con aula insuficiente suma penalizacion por CF") {
    val cursos = Vector(("C1", 0, 4, 50), ("C2", 5, 9, 10))
    val aulas  = Vector(("E1", 30), ("E2", 20))
    val dist   = Vector(Vector(0, 3), Vector(3, 0))
    val pesos  = (1000, 100, 1, 2)
    // CH=0, CF=1 (C1 no cabe en E1), DE=20-10=10, MV=3
    assert(costoAsignacion(cursos, aulas, dist, Vector(0, 1), pesos) == 116)
  }

  // generarAsignaciones
  test("generarAsignaciones: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignaciones(2, 2).length == 4)
  }

  test("generarAsignaciones: 3 cursos y 3 aulas produce 27 asignaciones") {
    assert(generarAsignaciones(3, 3).length == 27)
  }

  //
  test("generarAsignaciones: 0 cursos genera una asignacion vacia caso extremo") {
    assert(
      generarAsignaciones(0,2) == Vector(Vector()))
  }

  test("generarAsignaciones: 2 cursos y 3 aulas generan 9 asignaciones") {
    assert(
      generarAsignaciones(2,3).length == 9
    )
  }

  test("generarAsignaciones: 3 cursos y 2 aulas generan 8 asignaciones") {
    assert(
      generarAsignaciones(3,2).length == 8
    )
  }

  test("generarAsignaciones: contiene la asignacion Vector(1,1)") {
    val resultado = generarAsignaciones(2,2)
    assert(resultado.contains(Vector(1,1)))
  }

  test("generarAsignaciones: contiene todas las asignaciones para 1 curso y 3 aulas caso extremo") {
    assert(
      generarAsignaciones(1,3) ==
        Vector(
          Vector(0),
          Vector(1),
          Vector(2)
        ))
  }
  // asignacionOptima
  test("asignacionOptima: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptima(c1, a1, d1, w)
    assert(costo <= 37)
  }

  test("asignacionOptima: la asignacion optima no tiene cursos sin asignar") {
    val (asig, _) = asignacionOptima(c1, a1, d1, w)
    assert(asig.forall(a => a >= 0))
  }

  test("asignacionOptima: el costo optimo es menor al de [0,0,1] (1031)") {
    val (_, costo) = asignacionOptima(c1, a1, d1, w)
    assert(costo < 1031)
  }

  test("asignacionOptima: ejemplo 2, el costo optimo no supera el de [0,1,0,1] (155)") {
    val c2 = Vector(("F01", 0, 4, 40), ("F02", 4, 8, 25), ("F03", 8, 12, 50), ("F04", 12, 16, 15))
    val a2 = Vector(("S201", 45), ("S202", 30))
    val d2 = Vector(Vector(0, 5), Vector(5, 0))
    val (_, costo) = asignacionOptima(c2, a2, d2, (1000, 100, 1, 2))
    assert(costo <= 155)
  }

  test("asignacionOptima: con un solo curso, el costo optimo es el minimo entre las aulas disponibles") {
    val cursos = Vector(("C1", 0, 4, 20))
    val aulas  = Vector(("E1", 30), ("E2", 50))
    val dist   = Vector(Vector(0, 3), Vector(3, 0))
    val (_, costo) = asignacionOptima(cursos, aulas, dist, (1000, 100, 1, 2))
    // la mejor es E1 con desperdicio 10, E2 tiene desperdicio 30
    assert(costo == 10)
  }

  test("asignacionOptima: la optima del ejemplo 1 tiene costo menor o igual a cualquier otra asignacion") {
    val (asigOptima, costoOptimo) = asignacionOptima(c1, a1, d1, w)
    val todasLasAsignaciones = generarAsignaciones(c1.length, a1.length)
    val todosLosCostos = todasLasAsignaciones.map(a => costoAsignacion(c1, a1, d1, a, w))
    assert(todosLosCostos.forall(c => costoOptimo <= c))
  }
}
