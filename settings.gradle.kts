rootProject.name = "stock"

fun defineSubProject(name: String, path: String) {
    include(name)
    project(":$name").projectDir = file(path)
}

defineSubProject("stock-app", "app")