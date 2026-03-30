import { BrowserRouter, Routes } from 'react-router-dom'
import Navbar from './components/common/Navbar'
import Footer from './components/common/Footer'

function App() {
  return (
    <BrowserRouter>
      <div className='App'>
        <Navbar />
        <div className="content">
            {/* <Routes>

            </Routes> */}
        </div>
        <Footer />
      </div>
    </BrowserRouter>
  )
}

export default App
