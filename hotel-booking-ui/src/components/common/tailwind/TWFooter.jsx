import { SiFacebook, SiInstagram, SiX } from '@icons-pack/react-simple-icons';

const TWFooter = () => {
    return (
        <footer className="bg-gray-900 border-t border-gray-800 w-full mt-auto">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16 text-sm text-gray-400">
                    
                    {/* Left: Brand */}
                    <div className="font-semibold text-gray-200">
                        Carl <span className="text-blue-500">Hotel</span>
                    </div>

                    {/* Center: Copyright */}
                    <div className="hidden md:block">
                        &copy; {new Date().getFullYear()} All rights reserved.
                    </div>

                    {/* Right: Social Media Icons */}
                    <div className="flex items-center space-x-6">
                        <a href="https://instagram.com" target="_blank" rel="noreferrer" 
                           className="hover:text-[#E4405F] transition-colors duration-200">
                            <SiInstagram size={18} />
                        </a>
                        <a href="https://facebook.com" target="_blank" rel="noreferrer" 
                           className="hover:text-[#1877F2] transition-colors duration-200">
                            <SiFacebook size={18} />
                        </a>
                        <a href="https://x.com" target="_blank" rel="noreferrer" 
                           className="hover:text-white transition-colors duration-200">
                            <SiX size={18} />
                        </a>
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default TWFooter;