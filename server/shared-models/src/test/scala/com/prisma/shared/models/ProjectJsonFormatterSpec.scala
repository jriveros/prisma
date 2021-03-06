package com.prisma.shared.models

import com.prisma.gc_values.StringGCValue
import com.prisma.utils.json.JsonUtils
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

class ProjectJsonFormatterSpec extends FlatSpec with Matchers with JsonUtils {
  import ProjectJsonFormatter._

  "Relation Fields" should "be readable in the obsolete format with inline relation objects" in {
    val json =
      """
        |{
        |    "name": "field_name",
        |    "typeIdentifier": "Relation",
        |    "isRequired": false,
        |    "isList": true,
        |    "isUnique": false,
        |    "isHidden": false,
        |    "isReadonly": false,
        |    "relation": {
        |        "name": "RelationName",
        |        "modelAId": "ModelA",
        |        "modelBId": "ModelB",
        |        "modelAOnDelete": "SET_NULL",
        |        "modelBOnDelete": "SET_NULL"
        |    },
        |    "relationSide": "B",
        |    "constraints": []
        |}
      """.stripMargin.parseJson

    val result = json.as[FieldTemplate]
    result.name should equal("field_name")
    result.typeIdentifier should equal(TypeIdentifier.Relation)
    result.isRequired should be(false)
    result.isList should be(true)
    result.isUnique should be(false)
    result.isHidden should be(false)
    result.isReadonly should be(false)
    result.isAutoGenerated should be(false)
    result.relationName should be(Some("RelationName"))
    result.relationSide should be(Some(RelationSide.B))
  }

  "Relation Fields" should "be readable in the current format with references to relation names only" in {
    val json =
      """
        |{
        |    "name": "field_name",
        |    "typeIdentifier": "Relation",
        |    "isRequired": false,
        |    "isList": true,
        |    "isUnique": false,
        |    "isHidden": false,
        |    "isReadonly": false,
        |    "relationName": "RelationName",
        |    "relationSide": "B",
        |    "constraints": []
        |}
      """.stripMargin.parseJson

    val result = json.as[FieldTemplate]
    result.name should equal("field_name")
    result.typeIdentifier should equal(TypeIdentifier.Relation)
    result.isRequired should be(false)
    result.isList should be(true)
    result.isUnique should be(false)
    result.isHidden should be(false)
    result.isReadonly should be(false)
    result.isAutoGenerated should be(false)
    result.relationName should be(Some("RelationName"))
    result.relationSide should be(Some(RelationSide.B))
  }

  "Scalar Fields" should "be readable" in {
    val json =
      """
        |{
        |    "name": "field_name",
        |    "typeIdentifier": "String",
        |    "isRequired": false,
        |    "isList": true,
        |    "isUnique": false,
        |    "isHidden": false,
        |    "isReadonly": false,
        |    "constraints": []
        |}
      """.stripMargin.parseJson

    val result = json.as[FieldTemplate]
    result.name should equal("field_name")
    result.typeIdentifier should equal(TypeIdentifier.String)
    result.isRequired should be(false)
    result.isList should be(true)
    result.isUnique should be(false)
    result.isHidden should be(false)
    result.isReadonly should be(false)
    result.isAutoGenerated should be(false)
    result.relationName should be(None)
    result.relationSide should be(None)
  }

  "Fields" should "always write the current format" in {
    val field = FieldTemplate(
      name = "field_name",
      typeIdentifier = TypeIdentifier.Relation,
      isRequired = true,
      isList = false,
      isUnique = false,
      isHidden = true,
      isReadonly = false,
      isAutoGenerated = true,
      enum = None,
      defaultValue = Some(StringGCValue("string_gc_value")),
      relationName = Some("RelationName"),
      relationSide = Some(RelationSide.A),
      manifestation = None
    )
    val json = Json.toJson(field).prettyPrint
    json should equal(
      s"""
        |{
        |    "name": "${field.name}",
        |    "typeIdentifier": "Relation",
        |    "isRequired": ${field.isRequired},
        |    "isList": ${field.isList},
        |    "isUnique": ${field.isUnique},
        |    "isHidden": ${field.isHidden},
        |    "isReadonly": ${field.isReadonly},
        |    "isAutoGenerated": true,
        |    "defaultValue" : {
        |        "gcValueType" : "string",
        |        "isList" : false,
        |        "value" : "string_gc_value"
        |    },
        |    "relationName": "${field.relationName.get}",
        |    "relationSide": "${field.relationSide.get.toString}"
        |}
      """.stripMargin.parseJson.prettyPrint
    )
  }
}
