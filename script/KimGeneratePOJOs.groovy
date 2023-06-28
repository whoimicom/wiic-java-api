import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil
import java.io.*
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */
packageName = ""
typeMapping = [
        (~/(?i)tinyint|smallint|mediumint/)      : "Integer",
        (~/(?i)int/)                             : "Long",
        (~/(?i)bool|boolean|bit|tinyint/)        : "Boolean",
        (~/(?i)float|double|real/)               : "Double",
        (~/(?i)number|decimal/)                  : "java.math.BigDecimal",
        (~/(?i)datetime|timestamp/)              : "java.time.LocalDateTime",
        (~/(?i)date/)                            : "java.time.LocalDate",
        (~/(?i)time/)                            : "java.time.LocalTime",
        (~/(?i)blob|binary|bfile|clob|raw|image/): "java.io.InputStream",
        (~/(?i)/)                                : "String"
]


FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable && it.getKind() == ObjectKind.TABLE }.each { generate(it, dir) }
}

def generate(table, dir) {
    def className = javaName(table.getName(), true)
    def fields = calcFields(table)
    packageName = getPackageName(dir)
    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(dir.toString(), className + ".java")), StandardCharsets.UTF_8))
    printWriter.withPrintWriter { out -> generate(out, className, fields, table) }

//    new File(dir, className + ".java").withPrintWriter { out -> generate(out, className, fields,table) }
}

def getPackageName(dir) {
    return dir.toString().replaceAll("\\\\", ".").replaceAll("/", ".").replaceAll("^.*src(\\.main\\.java\\.)?", "") + ";"
}

def generate(out, className, fields, table) {
    out.println "package $packageName"
    out.println ""
    out.println "import javax.persistence.*;"
    out.println "import java.io.Serializable;"
    def importPackages = [""] as HashSet
    fields.each() {
        def type = it.type
        def split = type.split("\\.")
        if (split.size() > 1) {
            importPackages += type
        }
    }
    importPackages.each() {
        if (!"".equals(it)) {
            out.println "import $it;"
        }
    }

    out.println ""
    out.println "/**\n" +
            " * @author kin.kim \n" +
            " * @date " + LocalDateTime.now() + " \n" +
            " */"
    out.println ""

    out.println "@Entity"
    out.println "@Table (name =\"" + table.getName() + "\")"
    out.println "public class $className  implements Serializable {"
    out.println ""
    out.println genSerialID()

    fields.each() {
        out.println ""
        if (isNotEmpty(it.commoent)) {
            out.println "\t/**"
            out.println "\t * ${it.commoent.toString()}"
            out.println "\t */"
        }

        if ("id".equalsIgnoreCase(it.colName) || it.primary) {
            out.println "\t@Id"
        }
        if (it.annos != "") out.println "   ${it.annos}"
        def type = it.type
        if ("LocalDateTime".equalsIgnoreCase(type)) {
            out.println '''\t@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")'''
        }
        if ("LocalTime".equalsIgnoreCase(type)) {
            out.println '''\t@JsonFormat(pattern = "HH:mm:ss")'''
        }
        if ("LocalDate".equalsIgnoreCase(type)) {
            out.println '''\t@JsonFormat(pattern = "yyyy-MM-dd")'''
        }

        out.println "\tprivate ${type.split("\\.").getAt(-1)} ${it.name};"

    }
    fields.each() {
        out.println ""
        out.println "\tpublic ${it.type} get${it.name.capitalize()}() {"
        out.println "\t\treturn this.${it.name};"
        out.println "\t}"
        out.println ""

        out.println "\tpublic void set${it.name.capitalize()}(${it.type} ${it.name}) {"
        out.println "\t\tthis.${it.name} = ${it.name};"
        out.println "\t}"
    }
    out.println ""
    out.println "}"
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        def comm = [
                colName : col.getName(),
                name    : javaName(col.getName(), false),
                type    : typeStr,
                commoent: col.getComment(),
                primary : DasUtil.isPrimary(col),
                annos   : "\t@Column(name = \"" + col.getName() + "\" )"]
        fields += [comm]
    }
}

def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

def isNotEmpty(content) {
    return content != null && content.toString().trim().length() > 0
}

static String changeStyle(String str, boolean toCamel) {
    if (!str || str.size() <= 1)
        return str

    if (toCamel) {
        String r = str.toLowerCase().split('_').collect { cc -> Case.LOWER.apply(cc).capitalize() }.join('')
        return r[0].toLowerCase() + r[1..-1]
    } else {
        str = str[0].toLowerCase() + str[1..-1]
        return str.collect { cc -> ((char) cc).isUpperCase() ? '_' + cc.toLowerCase() : cc }.join('')
    }
}

static String genSerialID() {
    return "\tprivate static final long serialVersionUID =  " + Math.abs(new Random().nextLong()) + "L;"
}
