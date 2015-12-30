class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"( // por defecto va al escritorio del EHR Server
            controller: 'committer',
            action: 'index'
        )
        "500"(view:'/error')
	}
}
