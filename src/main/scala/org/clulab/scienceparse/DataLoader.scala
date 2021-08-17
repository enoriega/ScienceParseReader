package org.clulab.scienceparse
import ai.lum.common.FileUtils._

import java.io.{File, FilenameFilter}

/**
 * This DataLoader abstract class is intended to be able to load information from files, with each file providing
 * a sequence of Strings.
 */
abstract class DataLoader {
  val extension: String
  def loadFile(f: File): Seq[String]
  def loadFile(filename: String): Seq[String] = loadFile(new File(filename))
  //  // defaultExtension can always be overridden, but will hopefully make calls to this method easier...?
  //  def loadCollection(collectionDir: String, extension: String = defaultExtension): Seq[Seq[String]] = findFiles(collectionDir, extension).map(loadFile)
}



class ScienceParsedDataLoader extends DataLoader {
  /**
   * Loader for documents which have been pre-processed with science parse (v1).  Each file contains a json representation
   * of the paper sections, here we will return the strings from each section as a Seq[String].
   *
   * @param f the File being loaded
   * @return string content of each section in the parsed pdf paper (as determined by science parse)
   */
  def loadFile(f: File): Seq[String] = {
    // todo: this approach should like be revisited to handle sections more elegantly, or to omit some, etc.
    //the heading and the text of the section are currently combined; might need to be revisted
    val scienceParseDoc = ScienceParseClient.mkDocument(f)
    if (scienceParseDoc.sections.isDefined)  {
      scienceParseDoc.sections.get.map(_.headingAndText) ++ scienceParseDoc.abstractText
    } else scienceParseDoc.abstractText.toSeq
  }
  override val extension: String = "json"
}

object ScienceParseClient {


  //------------------------------------------------------
  //     Methods for creating ScienceParseDocuments
  //------------------------------------------------------

  def mkDocument(file: File): ScienceParseDocument = {
    val json = ujson.read(file.readString())
    mkDocument(json("metadata"), file.getName)
  }

  def mkDocuments(dir: File): Seq[ScienceParseDocument] = {
    for(file <- dir.listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = name.toLowerCase.endsWith(".json")
    })) yield {
      mkDocument(file)
    }
  }

  def mkDocument(json: ujson.Js, id:String): ScienceParseDocument = {
//    val id = json("id").str
//    val title = json.obj.get("title").map(_.str)
//    val year = json.obj.get("year")
//    val authors = json("authors").arr.map(mkAuthor).toVector

    val abstractText = json.obj.get("abstractText") match {
      case Some(a) if a != ujson.Null => a.toString()
      case _ => ""
    }
    val sections = {
      if (json.obj.get("sections").nonEmpty) {
        if(json("sections") != ujson.Null)
          Some(json("sections").arr.map(mkSection).toVector)
        else None
      }
      else None
    }
//    val references = json("references").arr.map(mkReference).toVector
    ScienceParseDocument(id, None, None, Vector(), Some(abstractText), sections, Vector())
  }

  def mkAuthor(json: ujson.Js): Author = {
    val name = json("name").str
    val affiliations = json("affiliations").arr.map(_.str).toVector
    Author(name, affiliations)
  }

  //new line is there to make sure comment-like sections are not concatenated into sentences
  //textEnginePreprocessor substitutes \n with a period or space downstream
  def mkSection(json: ujson.Js): Section = {
    val heading = json.obj.get("heading") match {
      case Some(s) => s.toString()
      case None => ""
    }
    val text = json("text").str
    val headingAndText = {
      if (heading == "null")
        text
      else
        heading + "\n" + text
    }
    Section(headingAndText)
  }

  def mkReference(json: ujson.Js): Reference = {
    val title = json("title").str
    val authors = json("authors").arr.map(_.str).toVector
    val venue = json("venue").str
    val venueOption = json.obj.get("venue").map(_.str)
    val yearOption = json.obj.get("year").map(_.num.toInt)
    Reference(title, authors, venueOption, yearOption)
  }
}