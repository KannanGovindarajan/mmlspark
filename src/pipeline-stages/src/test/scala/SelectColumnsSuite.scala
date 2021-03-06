// Copyright (C) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in project root for information.

package com.microsoft.ml.spark

import org.apache.spark.ml.Transformer
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StructType

class SelectColumnsSuite extends TransformerFuzzingTest {

  import session.implicits._

  test("Select all columns in a data frame") {
    val input = makeBasicDF()
    val result = new SelectColumns()
      .setCols(Array("numbers", "words", "more"))
      .transform(input)
    assert(verifyResult(input, result))
  }

  test("Test: Select two columns in a data frame") {
    val expected = Seq(
      ("guitars", "drums"),
      ("piano", "trumpet"),
      ("bass", "cymbals")
    ).toDF("words", "more")
    val result = new SelectColumns()
      .setCols(Array("words", "more"))
      .transform(makeBasicDF())
    assert(verifyResult(expected, result))
  }

  test("Test: Select columns with spaces") {
    val expected = Seq(
      ("guitars", "drums"),
      ("piano", "trumpet"),
      ("bass", "cymbals")
    ).toDF("words", "Scored Labels")
    val result = new SelectColumns()
      .setCols(Array("words", "Scored Labels"))
      .transform(makeBasicDF().withColumnRenamed("more", "Scored Labels"))
    assert(verifyResult(expected, result))
  }

  test("Test: Select one column from the data frame") {
    val expected = Seq(
      "guitars",
      "piano",
      "bass"
    ).toDF("words")
    val result = new SelectColumns()
      .setCols(Array("words"))
      .transform(makeBasicDF())
    assert(verifyResult(expected, result))
  }

  test("Invalid column specified") {
    try {
      new SelectColumns().setCol("four").transform(makeBasicDF())
      fail()
    } catch {
      case _: NoSuchElementException =>
    }
  }

  override def setParams(fitDataset: DataFrame, transformer: Transformer): Transformer =
    transformer.asInstanceOf[SelectColumns].setCols(fitDataset.columns)

  override def createDataset: DataFrame = makeBasicDF()

  override def schemaForDataset: StructType = ???

  override def getTransformer(): Transformer = new SelectColumns()
}
