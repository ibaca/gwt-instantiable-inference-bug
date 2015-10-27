## GWT Instantiable Inference Bug

This compilation works 

    mvn clean package -Dgwt.style=PRETTY -Dgwt.draftCompile=true
    
This compilation does NOT work (if the commented code is uncommented, it works too)

    mvn clean package -Dgwt.style=PRETTY -Dgwt.draftCompile=false
    
