package org.clulab.scienceparse

import java.io.{File, PrintWriter}

/**
 * Converts ScienceParse json files in a directory into plain text files to be processed by REACH.
 * Arg 1: Input directory
 * Arg 2 (Optional): Output directory. If unspecified, it is CWD
 */
object ScienceParse2Text extends App {
  // Parse the files from the input directory
  val docs = ScienceParseClient.mkDocuments(new File(args(0)))
  // Save them to the output directory

  val outDir =
    if (args.length > 1)
      new File(args(1))
    else
      new File("") // This is the CWD

  for (doc <- docs.par) { // Do it in parallel
    try {
      // Get the text elements
      val abs = doc.abstractText match {
        case Some(txt) => txt
        case None => ""
      }

      val title = doc.title match {
        case Some(t) => t
        case None => ""
      }

      val body = doc.sections match {
        case Some(sections) =>
          sections.map(section => section.headingAndText).mkString("\n")
        case None => ""
      }

      // Put together the text
      val text = title + "\n" + abs + "\n" + body

      // Generate the file name based on the input name
      val fileName = doc.id.replace(".json", ".txt")

      // Save the text in the output directory
      val outputFile = new File(outDir, fileName)
      val pw = new PrintWriter(outputFile)
      pw.print(text)
      pw.close()
    } catch {
      case ex: Exception =>
        println(s"Problem with file ${doc.id}:\n$ex")
    }
  }

}
