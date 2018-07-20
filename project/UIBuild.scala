import sbt._
import sbt.internal.util.ManagedLogger
import scala.sys.process._

object UIBuild {

  val success = 0 // 0 exit code
  val error = 1 // 1 exit code

  def buildUiWatch(implicit dir: File, logger: ManagedLogger): Boolean = {
    if (runNpmInstall && verifyNode(8)) {
      logger.info("node_modules available")
      validateExecution("npm" :: "run" :: "build-watch" :: Nil)
    }
    else {
      val node = executeCommand("node" :: "-v" :: Nil)
      val npm = executeCommand("npm" :: "-v" :: Nil)
      logger.err(s"node:$node npm:$npm UI Build failed")
      false
    }
  }

  def buildUiProd(implicit dir: File, logger: ManagedLogger): Boolean = {
    if (runNpmInstall && verifyNode(8)) {
      validateExecution("npm" :: "run" :: "build-prod" :: Nil)
    }
    else {
      val node = executeCommand("node" :: "-v" :: Nil)
      val npm = executeCommand("npm" :: "-v" :: Nil)
      logger.err(s"node:$node npm:$npm UI Build failed")
      false
    }
  }

  def runNpmInstall(implicit dir: File, logger: ManagedLogger): Boolean = {
    if (!uiWasInstalled) {
      validateExecution("npm" :: "install" :: Nil)
    } else {
      true
    }
  }

  def verifyNode(version: Int)(implicit dir: File, logger: ManagedLogger): Boolean = {
    val nodeVersion = executeCommand("node" :: "-v" :: Nil)
    nodeVersion.replaceAll("[vV]", "").split("\\.").head.toInt == version
  }

  def uiWasInstalled(implicit dir: File): Boolean = {
    (dir / "node_modules").exists()
  }

  private def executeCommand(script: Seq[String])(implicit dir: File, logger: ManagedLogger): String = {
    logger.info(s"executing command:[${script.mkString(" ")}] in dir:[$dir] ")
    Process(script, dir) !!
  }

  private def validateExecution(script: Seq[String])(implicit dir: File, logger: ManagedLogger): Boolean = {
    logger.info(s"executing command:[${script.mkString(" ")}] in dir:[$dir] ")
    (Process(script, dir) !) == success
  }
}
