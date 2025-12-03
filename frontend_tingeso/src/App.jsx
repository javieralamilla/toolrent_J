import './App.css'
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import { useState } from 'react'
import Dashboard from './components/Dashboard.jsx';
import { useKeycloak } from "@react-keycloak/web";
import Navbar from "./components/Navbar.jsx"
import TopBar from "./components/TopBar.jsx";
import ToolsList from "./components/ToolsList.jsx";
import ToolsInventoryList from "./components/ToolsInventoryList.jsx";
import AddTool from "./components/AddTool.jsx";
import AddNewTool from './components/AddNewTool.jsx';
import AddExistingTool from './components/AddExistingTool.jsx';
import CustomersList from './components/CustomersList.jsx';
import AddCustomer from './components/AddCustomer.jsx';
import UpdateCustomer from './components/UpdateCustomer.jsx';
import LoansList from './components/LoansList.jsx';
import AddLoan from './components/AddLoan.jsx';
import LoanReturn from './components/LoanReturn.jsx';   
import GlobalRatesList from './components/GlobalRatesList.jsx';
import AddGlobalRates from './components/AddGlobalRates.jsx';
import UpdateRentals from './components/UpdateRentals.jsx';
import UpdateDailyRentalRate from './components/UpdateDailyRentalRate.jsx';
import UpdateReplacementValue from './components/UpdateReplacementValue.jsx';
import UpdateGlobalRates from './components/UpdateGlobalRates.jsx';
import KardexList from './components/KardexList.jsx';
import FineList from './components/FineList.jsx';
import AddFine from './components/AddFine.jsx';
import AddFineMinorDamage from './components/AddFineMinorDamage.jsx';
import AddFineIrreparableDamage from './components/AddFineIrreparableDamage.jsx';
import PayFine from './components/PayFine.jsx';
import RepairedTool from './components/RepairedTool.jsx';
import ActiveLoansList from './components/ActiveLoansList.jsx';
import CustomersWithOverdueList from './components/CustomersWithOverdueList.jsx';
import ToolsRanking from './components/ToolsRanking.jsx';


function App() {
    const { keycloak, initialized } = useKeycloak();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    if (!initialized) return <div>Cargando...</div>;

    const isLoggedIn = keycloak.authenticated;
    const roles = keycloak.tokenParsed?.realm_access?.roles || [];



    const PrivateRoute = ({ element, rolesAllowed }) => {
        if (!isLoggedIn) {
            keycloak.login();
            return null;
        }
        if (rolesAllowed && !rolesAllowed.some(r => roles.includes(r))) {
            return <h2>No tienes permiso para ver esta página</h2>;
        }
        return element;
    };

    const handleSidebarToggle = () => {
        setSidebarOpen(!sidebarOpen);
    };

    const handleSidebarClose = () => {
        setSidebarOpen(false);
    };

    if (!isLoggedIn) {
        keycloak.login({
            redirectUri: window.location.origin + '/'
        });
        return null;
    }

    return (
        <Router>
            <div className="container">
                {/* TopBar con el botón de menú */}
                <TopBar onMenuToggle={handleSidebarToggle} />

                {/* Navbar es tu sidebar/menú lateral */}
                <Navbar
                    open={sidebarOpen}
                    onClose={handleSidebarClose}
                />
                <Routes>
                    <Route path="/" element={<Dashboard/>} />
                    <Route path="/dashboard" element={<Dashboard/>} />
                    <Route
                        path="/tools"
                        element={<PrivateRoute element={<ToolsList />} rolesAllowed={["EMPLOYEE","ADMIN"]} />}
                    />
                    <Route
                        path="/toolsInventory"
                        element={<PrivateRoute element={<ToolsInventoryList />} rolesAllowed={["EMPLOYEE","ADMIN"]} />}
                    />
                        <Route
                            path="/addTool"
                            element={<PrivateRoute element={<AddTool />} rolesAllowed={["ADMIN"]} />}
                        />
                    <Route
                        path="/addTool/new"
                        element={<PrivateRoute element={<AddNewTool />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/addTool/existing"
                        element={<PrivateRoute element={<AddExistingTool />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/repairedTool"
                        element={<PrivateRoute element={<RepairedTool />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/customers"
                        element={<PrivateRoute element={<CustomersList />} rolesAllowed={["EMPLOYEE","ADMIN"]} />}
                    />
                    <Route
                        path="/customers/add"
                        element={<PrivateRoute element={<AddCustomer />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/customers/update"
                        element={<PrivateRoute element={<UpdateCustomer />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/loans"
                        element={<PrivateRoute element={<LoansList />} rolesAllowed={["EMPLOYEE","ADMIN"]} />}
                    />
                    <Route
                        path="/loans/add"
                        element={<PrivateRoute element={<AddLoan />} rolesAllowed={["EMPLOYEE","ADMIN"]} />}
                    />
                    <Route
                        path="/loans/returns"
                        element={<PrivateRoute element={<LoanReturn />} rolesAllowed={["EMPLOYEE","ADMIN"]} />}
                    />
                    <Route
                        path="/globalRates"
                        element={<PrivateRoute element={<GlobalRatesList />} rolesAllowed={["EMPLOYEE","ADMIN"]} />}
                    />
                    <Route
                        path="/globalRates/add"
                        element={<PrivateRoute element={<AddGlobalRates />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/rates/update"
                        element={<PrivateRoute element={<UpdateRentals />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/updateDailyRentalRate"
                        element={<PrivateRoute element={<UpdateDailyRentalRate />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/updateReplacementValue"
                        element={<PrivateRoute element={<UpdateReplacementValue />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/updateGlobalRates"
                        element={<PrivateRoute element={<UpdateGlobalRates />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/records"
                        element={<PrivateRoute element={<KardexList />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />}
                    />
                    <Route
                        path="/fines"
                        element={<PrivateRoute element={<FineList />} rolesAllowed={["EMPLOYEE", "ADMIN"]} />}
                    />
                    <Route
                        path="/fines/add"
                        element={<PrivateRoute element={<AddFine />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/fines/addMinorDamage"
                        element={<PrivateRoute element={<AddFineMinorDamage />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/fines/addIrreparableDamage"
                        element={<PrivateRoute element={<AddFineIrreparableDamage />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/fines/payFine"
                        element={<PrivateRoute element={<PayFine />} rolesAllowed={["ADMIN"]} />}
                    />
                    <Route
                        path="/reportsAndConsultations"
                        element={<PrivateRoute element={<ActiveLoansList />} rolesAllowed={["EMPLOYEE","ADMIN"]} />}
                    />
                    <Route
                        path="/reportsAndConsultations/listCustomersWithOverdue"
                        element={<PrivateRoute element={<CustomersWithOverdueList />} rolesAllowed={["EMPLOYEE","ADMIN"]} />}
                    />
                    <Route
                        path="/reportsAndConsultations/toolsRanking"
                        element={<PrivateRoute element={<ToolsRanking />} rolesAllowed={["EMPLOYEE","ADMIN"]} />}
                    />
                </Routes>
            </div>
        </Router>
    )
}

export default App