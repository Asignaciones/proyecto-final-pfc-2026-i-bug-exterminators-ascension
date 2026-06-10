package proyecto
import org.scalameter._
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._
import AsignacionAulasPar._
import org.scalameter._

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
  //
  test("choquesPar: cuatro cursos en la misma aula generan 6 choques") {
    val cursos = Vector(
      ("C1",0,10,20),
      ("C2",1,11,20),
      ("C3",2,12,20),
      ("C4",3,13,20)
    )
    val asig = Vector(0,0,0,0)
    assert(choquesPar(cursos, asig) == 6)
  }
  //
  test("choquesPar: un solo choque entre cuatro cursos") {
    val cursos = Vector(
      ("C1",0,5,20),
      ("C2",2,7,20),
      ("C3",10,15,20),
      ("C4",20,25,20))
    val asig = Vector(0,0,0,0)
    assert(choquesPar(cursos, asig) == 1)
  }
  //
  test("choquesPar: tres cursos solapados en aula 0 y dos en aula 1") {
    val cursos = Vector(
      ("C1",0,6,20),
      ("C2",2,8,20),
      ("C3",4,10,20),
      ("C4",0,6,20),
      ("C5",3,9,20))
    val asig = Vector(0,0,0,1,1)
    assert(choquesPar(cursos, asig) == 4)
  }
  //
  test("choquesPar: cursos solapados pero asignados a aulas distintas") {
    val cursos = Vector(
      ("C1",0,10,20),
      ("C2",1,11,20),
      ("C3",2,12,20),
      ("C4",3,13,20))
    val asig = Vector(0,1,2,3)
    assert(choquesPar(cursos, asig) == 0)
  }
  //
  test("choquesPar: mezcla de cursos asignados y sin asignar") {
    val cursos = Vector(
      ("C1",0,6,20),
      ("C2",2,8,20),
      ("C3",4,10,20))
    val asig = Vector(0,-1,0)
    assert(choquesPar(cursos, asig) == 1)
  }

  test("desperdicioPar: asignacion [0,0,1] tiene desperdicio 25") {
    assert(desperdicioPar(c1, a1, Vector(0, 0, 1)) == 25)
  }
  //
  test("desperdicioPar: una mitad aporta todo el desperdicio") {
    val cursos = Vector(
      ("A",0,2,10),
      ("B",2,4,15),
      ("C",4,6,50),
      ("D",6,8,60)
    )
    val aulas = Vector(
      ("E1",30)
    )
    assert(desperdicioPar(cursos,aulas,Vector(0,0,0,0)) == 35)
  }

  //
  test("desperdicioPar: todos los cursos exceden la capacidad") {
    val cursos = Vector(
      ("A",0,2,60),
      ("B",2,4,70),
      ("C",4,6,80)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",40)
    )
    assert(desperdicioPar(cursos,aulas,Vector(0,1,0)) == 0)
  }

  //
  test("desperdicioPar: desperdicio repartido entre ambas mitades") {
    val cursos = Vector(
      ("A",0,2,10),
      ("B",2,4,20),
      ("C",4,6,15),
      ("D",6,8,25)
    )
    val aulas = Vector(
      ("E1",30)
    )
    assert(desperdicioPar(cursos,aulas,Vector(0,0,0,0)) == 50)
  }

  //
  test("desperdicioPar: un curso cabe exactamente y otro genera desperdicio") {
    val cursos = Vector(
      ("A",0,2,30),
      ("B",2,4,10)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",20)
    )
    assert(desperdicioPar(cursos,aulas,Vector(0,1)) == 10)
  }

  //
  test("desperdicioPar: cinco cursos en distintas aulas") {
    val cursos = Vector(
      ("A",0,2,10),
      ("B",2,4,15),
      ("C",4,6,20),
      ("D",6,8,25),
      ("E",8,10,30)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",35),
      ("E3",40)
    )
    assert(desperdicioPar(cursos,aulas,Vector(0,1,2,0,1)) == 70)
  }

  test("movilidadPar: asignacion [0,0,1] tiene movilidad 3") {
    assert(movilidadPar(c1, a1, d1, Vector(0, 0, 1)) == 3)
  }
  //
  test("movilidadPar: cuatro cursos alternando aulas generan movilidad 9") {
    val cursos = Vector(
      ("C1",0,2,20),
      ("C2",2,4,20),
      ("C3",4,6,20),
      ("C4",6,8,20)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",30)
    )
    val d = Vector(
      Vector(0,3),
      Vector(3,0)
    )
    assert(movilidadPar(cursos,aulas,d,Vector(0,1,0,1)) == 9)
  }

  //
  test("movilidadPar: todos los cursos en la misma aula generan movilidad 0") {
    val cursos = Vector(
      ("C1",0,2,20),
      ("C2",2,4,20),
      ("C3",4,6,20),
      ("C4",6,8,20)
    )
    val aulas = Vector(("E1",30))
    val d = Vector(
      Vector(0)
    )
    assert(movilidadPar(cursos,aulas,d,Vector(0,0,0,0)) == 0)
  }
  //
  test("movilidadPar: tres cursos con distancias distintas generan movilidad 7") {
    val cursos = Vector(
      ("C1",0,2,20),
      ("C2",2,4,20),
      ("C3",4,6,20)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",30),
      ("E3",30)
    )
    val d = Vector(
      Vector(0,2,5),
      Vector(2,0,5),
      Vector(5,5,0)
    )
    assert(movilidadPar(cursos,aulas,d,Vector(0,1,2)) == 7)
  }
  //
  test("movilidadPar: cursos sin asignar no participan en el calculo") {
    val cursos = Vector(
      ("C1",0,2,20),
      ("C2",2,4,20),
      ("C3",4,6,20)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",30)
    )
    val d = Vector(
      Vector(0,5),
      Vector(5,0)
    )
    assert(movilidadPar(cursos,aulas,d,Vector(0,-1,1)) == 5)
  }

  //
  test("movilidadPar: cinco cursos generan movilidad acumulada 12") {
    val cursos = Vector(
      ("C1",0,2,20),
      ("C2",2,4,20),
      ("C3",4,6,20),
      ("C4",6,8,20),
      ("C5",8,10,20)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",30)
    )
    val d = Vector(
      Vector(0,3),
      Vector(3,0)
    )
    assert(movilidadPar(cursos,aulas,d,Vector(0,1,0,1,0)) == 12)
  }

  test("generarAsignacionesPar: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignacionesPar(2, 2).length == 4)
  }

  //
  test("generarAsignacionesPar: 0 cursos devuelve una asignacion vacia") {
    assert(
      generarAsignacionesPar(0,5) == Vector(Vector()))
  }

  //

  test("generarAsignaciones: contiene la asignacion Vector(0,2,1)") {
    val resultado = generarAsignaciones(3,3)
    assert(
      resultado.contains(Vector(0,2,1)))
  }
  //
  test("generarAsignaciones: ninguna asignacion tiene aulas fuera del rango caso extremo") {
    val resultado = generarAsignaciones(3,4)
    assert(resultado.forall(asignacion =>
      asignacion.forall(aula => aula >= 0 && aula < 4)))
  }
   //
  test("generarAsignaciones: todas las asignaciones son diferentes") {
    val resultado = generarAsignaciones(2,3)
    assert(resultado.distinct.length == resultado.length)
  }
  //
  test("generarAsignaciones: 4 cursos y 2 aulas genera 16 asignaciones") {
    assert(generarAsignaciones(4,2).length == 16)
  }

  test("asignacionOptimaPar: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costo <= 37)
  }

  //Mediciones de las funciones en su version secuencial vs su version paralela
  def generarCursos(n:Int): Cursos =
    Vector.tabulate(n){ i =>
      (
        s"C$i",
        i,      // hora inicio
        i+3,    // hora fin
        20 + (i % 20))
    }
  def generarAsignacion(n:Int,m:Int): Asignacion =
    Vector.tabulate(n)(i => i % m)

  println("\nMedicion choques VS choquesPar")
  println("n\tSeq(ms)\tPar(ms)\tSpeedup")
  for(n <- 1 to 8){
    val cursos = generarCursos(n)
    val asig = generarAsignacion(n,5)
    val timeSeq = measure {
      choques(cursos,asig)
    }
    val timePar = measure {
      choquesPar(cursos,asig)
    }
    val speedup = timeSeq.value / timePar.value
    println(
      f"$n\t${timeSeq.value}%.4f\t${timePar.value}%.4f\t$speedup%.2f"
    )
  }

  println("\nMedicion Desperdicio VS DesperdicioPar")
  println("n\tSeq(ms)\tPar(ms)\tSpeedup")

  for(n <- 1 to 8){
    val cursos = generarCursos(n)
    val asig = generarAsignacion(n,a1.length)
    val timeSeq = measure {
      desperdicio(cursos,a1,asig)
    }
    val timePar = measure {
      desperdicioPar(cursos,a1,asig)
    }
    val speedup = timeSeq.value / timePar.value
    println(f"$n\t${timeSeq.value}%.4f\t${timePar.value}%.4f\t$speedup%.2f")
  }

  println("\nMedicio Movilidad VS MovilidadPAR")
  println("n\tSeq(ms)\tPar(ms)\tSpeedup")

  for(n <- 2 to 8){
    val cursos = generarCursos(n)
    val asig = generarAsignacion(n,a1.length)
    val timeSeq = measure {
      movilidad(cursos,a1,d1,asig)
    }
    val timePar = measure {
      movilidadPar(cursos,a1,d1,asig)
    }
    val speedup = timeSeq.value / timePar.value
    println(f"$n\t${timeSeq.value}%.4f\t${timePar.value}%.4f\t$speedup%.2f")
  }

  //
  println("\nMedicion generarAsignaciones vs generarAsignacionesPar")
  println("n\tm\tSec(ms)\tPar(ms)\tSpeedup")
  for {
    n <- 1 to 8 //Para generar varios valores para probar las funciones y hacer las mediciones correctamente
    m <- 2 to 5
  } {
    val seq = measure { //Medicion para la funcion con paralelizacion
      generarAsignaciones(n,m)
    }
    val par = measure {  //Medicion para la funcion con paralelizacion
      generarAsignacionesPar(n,m)
    }  //Calculo speedup
    val speedup = seq.value / par.value  //se obtiene el valor que da la paralelizacion y se divide la parte secuencial con la parte paralela
    println(s"$n\t$m\t$seq\t$par\t$speedup")
  }

  println("\nMedicion Funcion asignación óptima vs asignacionOptimaPar")
  def generarAulas(m:Int): Aulas =
    Vector.tabulate(m){ i =>
      (s"E$i", 30 + i*10)
    }
  def generarDistancias(m:Int): Distancias =
    Vector.tabulate(m){ i =>
      Vector.tabulate(m){ j =>
        math.abs(i-j)
      }
    }
  println("n\tm\tSeq(ms)\tPar(ms)\tSpeedup")
  for{
    n <- 2 to 8
    m <- 2 to 5
  }{
    val cursos = generarCursos(n)
    val aulas = generarAulas(m)
    val distancias = generarDistancias(m)
    val timeSeq = measure {
      asignacionOptima(cursos,aulas,distancias,w)
    }
    val timePar = measure {
      asignacionOptimaPar(cursos,aulas,distancias,w)
    }
    val speedup = timeSeq.value / timePar.value
    println(f"$n\t$m\t${timeSeq.value}%.4f\t${timePar.value}%.4f\t$speedup%.2f"
    )
  }

}
