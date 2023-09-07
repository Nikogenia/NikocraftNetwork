export default function ErrorPopup({error, errorMessage, errorSeconds}) {

    return (
        <div className={(errorSeconds > 0) ? "opacity-100 transition-opacity duration-300" : "opacity-0 transition-opacity duration-1000"}>
            <div className="fixed top-20 right-1 bg-red-700 rounded-xl flex flex-col p-2 z-50">
                <div className="text-3xl text-red-200 text-center">ERROR</div>
                <div className="text-xl text-red-200 text-center">{error}: {errorMessage}</div>
            </div>
        </div>
    )

}
