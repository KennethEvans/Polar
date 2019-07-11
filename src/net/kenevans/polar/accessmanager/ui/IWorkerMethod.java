package net.kenevans.polar.accessmanager.ui;

/**
 * IMethod Interface for passing a method.
 * 
 * @author Kenneth Evans, Jr.s
 */
interface IWorkerMethod
{
    /**
     * Designed to call different methods depending on the type parameter.
     * 
     * @param type Determines which method to call in the implemented work
     *            method.
     */
    void work(PolarAccessManager.BackgroundMethodType type);
}
