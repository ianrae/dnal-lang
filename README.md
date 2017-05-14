DNAL - Data Needs a Language
=======

A compiler for data. DNAL is a JSON-like language that includes types and data validation rules.  
The DNAL compiler validates and transforms your DNAL source into other data formats.

*Latest version*: 0.2.1 (requires Java 8+)

## News
2017-05-14
   Release v0.2.1 - 
   compile from stream, 
   validation options,
   improved date format support,
   prototype of views feature    
2017-03-14
    Release v0.2.0 - 
	'unique' keyword, 
	reflection-based loader,
	many fixes

## How to Use?

DNAL is available in maven-central. [User's Guide](https://dnal-lang.org/documentation/)

## Maven

Add the following fragment to your `<dependencies>` section:

      <dependency>
		<groupId>org.dnal-lang</groupId>
		<artifactId>dnal</artifactId>
		<packaging>jar</packaging>
		<version>0.2.1</version>
      </dependency>

## Tell me more

DNAL is a language for data.  It supports types, relations, and validation rules.  Here is a sample:

		//define a type
		type Person struct {
		    firstName string
		    lastName string
		    birthDate date
		  }
		  !empty(firstName)
		  !empty(lastName)
		end

		//define some values
		let customer Person = { 'Gillian', 'Smith', '1965-12-31' }
		
		let people list<Person> = [
		  { 'Simone', 'Tremblay', '1965-12-31' },
		  { 'Jason', 'Ackerman', '1991-01-15' }
		]

The *dnalc* compiler converts DNAL into other formats.  Use it to create JSON, XML, or to do code generation of bean or DTO classes.
The compiler is extensible; add your own output generators.

The DNAL API is a Java API for loading and querying DNAL data.

## Feature highlights

* all the normal scalar types (int, boolean, string, etc)
* lists and structs
* package and import
* type inheritance

## Uses

* producing valid configuration files (JSON, XML, SQL, etc).
* used as configuration directly by a program
* used for validating data received by a program.
* code generation for creating bean and DTO classes.


