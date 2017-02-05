#DNAL - Data Needs a Language
=======

A compiler for data. DNAL is a JSON-like language that includes types and data validation rules.  
The DNAL compiler validates and transforms your DNAL source into other data formats.


## How to Use?

DNAL is available in maven-central. [User's Guide](https://dnal-lang.org/documentation/)

## Maven

Add the following fragment to your `<dependencies>` section:

      <dependency>
		<groupId>org.dnal-lang</groupId>
		<artifactId>dnal</artifactId>
		<packaging>jar</packaging>
		<version>0.1.0</version>
      </dependency>

## Tell me more

DNAL is a language for data.  It supports types, relations, and validation rules.  Here is a sample:

		type Person struct {
		 firstName string
		 lastName string
		 birthDate date
		 }
		  !empty(firstName)
		  !empty(lastName)
		 end

		let people Person = [
		 { 'Gillian', 'Smith', '1965-12-31' },
		 { 'Jason', 'Ackerman', '1991-01-15' }
		]

The *dnalc* compiler converts DNAL into other formats such as JSON, XML, or to do code generation of bean or DTO classes.

The DNAL API is a Java API for loading and querying DNAL data.

## Feature highlights

* all the normal scalar types (int, boolean, string, etc)
* lists and structs
* package and import
* type inheritance

